package com.gitlab.walneyalves.meeting_rooms.controller.meeting;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.MeetingFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDescriptionRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDurationRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateTitleRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.MeetingResponse;
import com.gitlab.walneyalves.meeting_rooms.domain.response.RoomResponse;
import com.gitlab.walneyalves.meeting_rooms.exception.ExceptionAdvice;
import com.gitlab.walneyalves.meeting_rooms.exception.MeetingRoomsException;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import com.gitlab.walneyalves.meeting_rooms.service.MeetingService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@ExceptionAdvice
@RestController
@RequestMapping("/meeting")
public class MeetingController implements IMeetingController {

    private final RoomService roomService;

    private final MeetingService meetingService;

    @Override
    public ResponseEntity<Meeting> create(UUID host,
                                          UUID roomId,
                                          MeetingCreateRequest request) {
        val room = roomService.get(roomId).orElseThrow(() -> new MeetingRoomsException(RoomResponse.NOT_FOUND));
        if (request.startDate().isAfter(request.endDate())) {
            throw new MeetingRoomsException(MeetingResponse.INVALID_TIME_RANGE);
        }
        if (!roomService.isTimeRangeSupported(room, request.startDate(), request.endDate())) {
            throw new MeetingRoomsException(MeetingResponse.INVALID_TIME_RANGE);
        }
        if (!roomService.isAvailable(room.getId())) {
            throw new MeetingRoomsException(RoomResponse.UNAVAILABLE);
        }
        if (meetingService.hasMeeting(room, request.startDate(), request.endDate())) {
            throw new MeetingRoomsException(RoomResponse.UNAVAILABLE);
        }
        val meeting = meetingService.create(request, room, host);
        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }

    @Override
    public ResponseEntity<Page<Meeting>> getMeetings(Pageable pageable,
                                                     UUID host,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate,
                                                     Meeting.State... states) {
        val filter = new MeetingFilter(startDate, endDate, states);
        return ResponseEntity.ok(meetingService.getMeetings(host, filter, pageable));
    }

    @Override
    public ResponseEntity<?> cancel(UUID host,
                                    UUID meetingId) {
        val meeting = meetingService.get(meetingId).orElseThrow(() -> new MeetingRoomsException(MeetingResponse.NOT_FOUND));
        checkHost(meeting, host);
        if (!meetingService.canCancel(meeting)) {
            throw new MeetingRoomsException(MeetingResponse.INVALID_STATE);
        }
        meetingService.cancel(meeting);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Meeting> updateTitle(UUID host,
                                               UUID meetingId,
                                               MeetingUpdateTitleRequest request) {
        val meeting = meetingService.get(meetingId).orElseThrow(() -> new MeetingRoomsException(MeetingResponse.NOT_FOUND));
        checkHost(meeting, host);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.updateTitle(meeting, request.title()));
    }

    @Override
    public ResponseEntity<Meeting> updateDescription(UUID host,
                                                     UUID meetingId,
                                                     MeetingUpdateDescriptionRequest request) {
        val meeting = meetingService.get(meetingId).orElseThrow(() -> new MeetingRoomsException(MeetingResponse.NOT_FOUND));
        checkHost(meeting, host);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.updateDescription(meeting, request.description()));
    }

    @Override
    public ResponseEntity<Meeting> updateDuration(UUID host,
                                                  UUID meetingId,
                                                  MeetingUpdateDurationRequest request) {
        val meeting = meetingService.get(meetingId).orElseThrow(() -> new MeetingRoomsException(MeetingResponse.NOT_FOUND));
        checkHost(meeting, host);
        if (!meeting.getState().equals(Meeting.State.CREATED)) {
            throw new MeetingRoomsException(MeetingResponse.INVALID_STATE);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingService.updateDuration(meeting, request.startDate(), request.endDate()));
    }

    private void checkHost(Meeting meeting, UUID host) {
        if (!meeting.getHost().equals(host)) {
            throw new MeetingRoomsException(MeetingResponse.INSUFFICIENT_PRIVILEGES);
        }
    }
}