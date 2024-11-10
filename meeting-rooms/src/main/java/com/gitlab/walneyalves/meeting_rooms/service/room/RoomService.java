package com.gitlab.walneyalves.meeting_rooms.service.room;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.RoomFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.response.RoomResponse;
import com.gitlab.walneyalves.meeting_rooms.exception.MeetingRoomsException;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final MongoTemplate mongoTemplate;

    private final RoomRepository roomRepository;

    private final RoomResourceService roomResourceService;

    @Transactional
    public Room create(@NonNull Room.RoomBuilder builder) {
        return roomRepository.save(builder.build());
    }

    public Optional<Room> get(@NonNull UUID id) {
        return roomRepository.findById(id);
    }

    public Page<Room> getAll(@NonNull Pageable pageable, @NonNull RoomFilter filter) {
        val query = new Query();
        val availableFilter = filter.available();
        val endDateFilter = filter.finalAvailability();
        val startDateFilter = filter.initialAvailability();
        val capacityFilter = filter.capacity();
        if (Objects.nonNull(availableFilter)) {
            query.addCriteria(Criteria.where("available").is(availableFilter));
        }
        if (Objects.nonNull(endDateFilter) && Objects.nonNull(startDateFilter)) {
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("initialAvailability").lte(endDateFilter),
                    Criteria.where("finalAvailability").gte(startDateFilter))
            );
        }
        if (Objects.nonNull(capacityFilter)) {
            query.addCriteria(Criteria.where("capacity").is(capacityFilter));
        }
        val rooms = mongoTemplate.find(query, Room.class);
        val typesFilter = filter.types();
        val filteredRooms = rooms.stream()
                .map(room -> Pair.of(room, roomResourceService.getRoomResources(room)))
                .filter(pair -> pair
                        .getSecond()
                        .stream()
                        .allMatch(resource ->
                                (Objects.isNull(typesFilter) || typesFilter.length == 0) || stream(typesFilter)
                                        .anyMatch(resourceType -> resource.getType().equals(resourceType))))
                .map(Pair::getFirst)
                .toList();
        val start = (int) pageable.getOffset();
        val end = Math.min((start + pageable.getPageSize()), filteredRooms.size());
        return new PageImpl<>(filteredRooms.subList(start, end), pageable, filteredRooms.size());
    }

    public Boolean isAvailable(@NonNull UUID id) {
        return get(id).isPresent() ? get(id).get().getAvailable() : false;
    }

    public Boolean isTimeRangeSupported(@NonNull Room room,
                                        @NonNull LocalDateTime start,
                                        @NonNull LocalDateTime end) {
        val initialAvailability = room.getInitialAvailability();
        val finalAvailability = room.getFinalAvailability();
        val requestedStart = start.toLocalTime();
        val requestedEnd = end.toLocalTime();
        return !requestedStart.isBefore(initialAvailability) && !requestedEnd.isAfter(finalAvailability);
    }

    public Boolean isValidCapacity(Integer capacity) {
        return capacity >= Room.MIN_CAPACITY;
    }

    @Transactional
    public Room updateName(@NonNull Room room, @NonNull String name) {
        room.setName(name);
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateAvailabilityPeriod(@NonNull Room room,
                                         @NonNull LocalTime initialAvailability,
                                         @NonNull LocalTime finalAvailability) {
        room.setInitialAvailability(initialAvailability);
        room.setFinalAvailability(finalAvailability);
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateCapacity(@NonNull Room room, @NonNull Integer capacity) {
        if (room.getCapacity().equals(capacity) || !isValidCapacity(capacity)) {
            throw new MeetingRoomsException(RoomResponse.INVALID_CAPACITY);
        }
        room.setCapacity(capacity);
        return roomRepository.save(room);
    }

    @Transactional
    public void updateAvailable(@NonNull Room room, @NonNull Boolean available) {
        room.setAvailable(available);
        roomRepository.save(room);
    }

    @Transactional
    public void delete(@NonNull Room room) {
        roomRepository.deleteById(room.getId());
    }
}