package com.gitlab.walneyalves.meeting_rooms.service;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.MeetingFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.repository.MeetingRepository;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MongoTemplate mongoTemplate;

    private final RoomService roomService;

    private final MeetingRepository meetingRepository;

    @Async
    @Scheduled(cron = "0/3 * * * * *")
    public void startMeetingsTask() {
        val now = LocalDateTime.now();
        getMeetings(Meeting.State.CREATED)
                .filter(meeting -> !meeting.getStartDate().isAfter(now))
                .forEach(this::start);
    }

    @Async
    @Scheduled(cron = "0/3 * * * * *")
    public void endMeetingsTask() {
        val now = LocalDateTime.now();
        getMeetings(Meeting.State.IN_PROGRESS)
                .filter(meeting -> meeting.getEndDate().isBefore(now))
                .forEach(this::end);
    }

    @Transactional
    public Meeting create(MeetingCreateRequest request, Room room, UUID host) {
        val meeting = Meeting
                .builder()
                .description(request.description())
                .host(host)
                .title(request.title())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .roomId(room.getId())
                .build();
        return meetingRepository.save(meeting);
    }

    public Optional<Meeting> get(UUID id) {
        return meetingRepository.findById(id);
    }

    @Transactional
    protected void start(Meeting meeting) {
        updateState(meeting, Meeting.State.IN_PROGRESS);
        val room = roomService.get(meeting.getRoomId()).orElseThrow();
        roomService.updateAvailable(room, false);
    }

    @Transactional
    protected void end(Meeting meeting) {
        updateState(meeting, Meeting.State.ENDED);
        val room = roomService.get(meeting.getRoomId()).orElseThrow();
        roomService.updateAvailable(room, true);
    }

    @Transactional
    public void cancel(Meeting meeting) {
        updateState(meeting, Meeting.State.CANCELLED);
    }

    public Boolean canCancel(Meeting meeting) {
        return Stream
                .of(Meeting.State.IN_PROGRESS, Meeting.State.CREATED)
                .anyMatch(state -> state.equals(meeting.getState()));
    }

    public Boolean hasMeeting(Room room, LocalDateTime start, LocalDateTime end) {
        return getMeetings(Meeting.State.CREATED, Meeting.State.IN_PROGRESS)
                .filter(meeting -> meeting.getRoomId().equals(room.getId()))
                .anyMatch(meeting -> {
                    val startDate = meeting.getStartDate();
                    val endDate = meeting.getEndDate();
                    return !(end.isBefore(startDate) || start.isAfter(endDate));
                });
    }

    public Page<Meeting> getMeetings(UUID host, MeetingFilter filter, Pageable pageable) {
        val query = new Query();
        val endDateFilter = filter.endDate();
        val startDateFilter = filter.startDate();
        val states = filter.states();
        query.addCriteria(Criteria.where("host").is(host));
        if (Objects.nonNull(endDateFilter) && Objects.nonNull(startDateFilter)) {
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("startDate").lte(endDateFilter),
                    Criteria.where("endDate").gte(startDateFilter))
            );
        }
        if (Objects.nonNull(states)) {
            query.addCriteria(Criteria.where("state").in(Arrays.asList(states)));
        }
        val meetings = mongoTemplate.find(query, Meeting.class);
        val start = (int) pageable.getOffset();
        val end = Math.min((start + pageable.getPageSize()), meetings.size());
        return new PageImpl<>(meetings.subList(start, end), pageable, meetings.size());
    }

    public Stream<Meeting> getMeetings(Meeting.State... includedStates) {
        return meetingRepository.findAllByStateIsIn(stream(includedStates).toList());
    }

    @Transactional
    public Meeting updateTitle(@NonNull Meeting meeting, @NonNull String title) {
        meeting.setTitle(title);
        return meetingRepository.save(meeting);
    }

    @Transactional
    public Meeting updateDescription(@NonNull Meeting meeting, @Nullable String description) {
        meeting.setDescription(description);
        return meetingRepository.save(meeting);
    }

    @Transactional
    public Meeting updateDuration(@NonNull Meeting meeting, @NonNull LocalDateTime start, @NonNull LocalDateTime end) {
        meeting.setStartDate(start);
        meeting.setEndDate(end);
        return meetingRepository.save(meeting);
    }

    @Transactional
    protected void updateState(@NonNull Meeting meeting, @NonNull Meeting.State state) {
        meeting.setState(state);
        meetingRepository.save(meeting);
    }
}