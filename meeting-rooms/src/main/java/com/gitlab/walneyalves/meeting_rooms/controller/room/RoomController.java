package com.gitlab.walneyalves.meeting_rooms.controller.room;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.RoomFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateAvailabilityPeriodRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateCapacityRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.ResourceResponse;
import com.gitlab.walneyalves.meeting_rooms.domain.response.RoomResponse;
import com.gitlab.walneyalves.meeting_rooms.exception.MeetingRoomsException;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/room")
public class RoomController implements IRoomController {

    private final RoomService roomService;
    private final RoomResourceService roomResourceService;
    private final ResourceService resourceService;

    @Override
    public ResponseEntity<Room> create(RoomCreateRequest request) {
        if (request.initialAvailability().isAfter(request.finalAvailability())) {
            throw new MeetingRoomsException(RoomResponse.INVALID_AVAILABILITY_PERIOD);
        }
        if (!roomService.isValidCapacity(request.capacity())) {
            throw new MeetingRoomsException(RoomResponse.INVALID_CAPACITY);
        }
        val room = roomService.create(Room.builder()
                .name(request.name())
                .initialAvailability(request.initialAvailability())
                .finalAvailability(request.finalAvailability())
                .capacity(request.capacity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @Override
    public ResponseEntity<Page<Room>> getRooms(Pageable pageable,
                                               Integer capacity,
                                               Boolean available,
                                               LocalTime initialAvailability,
                                               LocalTime finalAvailability,
                                               Resource.Type... types) {
        val filter = new RoomFilter(capacity, available, initialAvailability, finalAvailability, types);
        return ResponseEntity.ok(roomService.getAll(pageable, filter));
    }

    @Override
    public ResponseEntity<List<Resource>> getRoomResources(UUID roomId) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        return ResponseEntity.ok(roomResourceService.getRoomResources(room));
    }

    @Override
    public ResponseEntity<Room> updateName(UUID roomId, RoomUpdateNameRequest request) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        checkAvailability(room);
        return ResponseEntity.ok(roomService.updateName(room, request.name()));
    }

    @Override
    public ResponseEntity<Room> updateCapacity(UUID roomId, RoomUpdateCapacityRequest request) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        checkAvailability(room);
        return ResponseEntity.ok(roomService.updateCapacity(room, request.capacity()));
    }

    @Override
    public ResponseEntity<Room> updateAvailabilityPeriod(UUID roomId, RoomUpdateAvailabilityPeriodRequest request) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        return ResponseEntity.ok(roomService.updateAvailabilityPeriod(room, request.initialAvailability(), request.finalAvailability()));
    }

    @Override
    public ResponseEntity<Room> addResource(UUID roomId, UUID resourceId) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        val resource = resourceService.get(resourceId).orElseThrow(() -> new MeetingRoomsException(ResourceResponse.NOT_FOUND));
        checkAvailability(room);
        roomResourceService.addResource(room, resource);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Room> removeResource(UUID roomId, UUID resourceId) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        val resource = resourceService.get(resourceId).orElseThrow(() -> new MeetingRoomsException(ResourceResponse.NOT_FOUND));
        checkAvailability(room);
        roomResourceService.removeResource(room, resource);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> removeResources(UUID roomId) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        checkAvailability(room);
        roomResourceService.removeResources(room);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> delete(UUID roomId) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        checkAvailability(room);
        roomService.delete(room);
        return ResponseEntity.noContent().build();
    }

    private void checkAvailability(Room room) {
        if (!roomService.isAvailable(room.getId())) {
            throw new MeetingRoomsException(RoomResponse.UNAVAILABLE);
        }
    }
}