package com.gitlab.walneyalves.meeting_rooms.tests.service;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.MeetingFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.repository.MeetingRepository;
import com.gitlab.walneyalves.meeting_rooms.service.MeetingService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTests {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MeetingService meetingService;

    private UUID meetingId;

    private UUID hostId;

    private UUID roomId;

    private Meeting meeting;

    private Meeting meetingCreated;

    private Room room;

    private MeetingCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        hostId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        room = Room
                .builder()
                .id(roomId)
                .name("Main Room")
                .initialAvailability(LocalTime.of(8, 0, 0))
                .finalAvailability(LocalTime.of(18, 0, 0))
                .available(true)
                .build();
        meeting = Meeting.builder()
                .id(meetingId)
                .host(hostId)
                .title("Test Meeting")
                .roomId(roomId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .build();
        meetingCreated = Meeting.builder()
                .id(UUID.randomUUID())
                .roomId(roomId)
                .host(hostId)
                .title("New Meeting")
                .state(Meeting.State.CREATED)
                .startDate(LocalDateTime.now().minusMinutes(10))
                .endDate(LocalDateTime.now().plusMinutes(30))
                .build();
        createRequest = new MeetingCreateRequest("New Meeting", null, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
    }

    @Test
    void testCreateMeeting() {
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meetingCreated);
        val createdMeeting = meetingService.create(createRequest, room, hostId);
        assertNotNull(createdMeeting);
        assertEquals("New Meeting", createdMeeting.getTitle());
        assertEquals(roomId, createdMeeting.getRoomId());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    void testGetMeetingByIdFound() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        val foundMeeting = meetingService.get(meetingId);
        assertTrue(foundMeeting.isPresent());
        assertEquals(meetingId, foundMeeting.get().getId());
        verify(meetingRepository, times(1)).findById(meetingId);
    }

    @Test
    void testGetMeetingByIdNotFound() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());
        val foundMeeting = meetingService.get(meetingId);
        assertFalse(foundMeeting.isPresent());
        verify(meetingRepository, times(1)).findById(meetingId);
    }

    @Test
    void testUpdateMeetingTitle() {
        val newTitle = "Updated Title";
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        val updatedMeeting = meetingService.updateTitle(meeting, newTitle);
        assertEquals(newTitle, updatedMeeting.getTitle());
        verify(meetingRepository, times(1)).save(meeting);
    }

    @Test
    void testUpdateMeetingDescription() {
        val newDescription = "Updated Description";
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        val updatedMeeting = meetingService.updateDescription(meeting, newDescription);
        assertEquals(newDescription, updatedMeeting.getDescription());
        verify(meetingRepository, times(1)).save(meeting);
    }

    @Test
    void testCancelMeeting() {
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        meetingService.cancel(meeting);
        assertEquals(Meeting.State.CANCELLED, meeting.getState());
        verify(meetingRepository, times(1)).save(meeting);
    }

    @Test
    void testCanCancelMeeting() {
        meeting.setState(Meeting.State.CREATED);
        assertTrue(meetingService.canCancel(meeting));
        meeting.setState(Meeting.State.ENDED);
        assertFalse(meetingService.canCancel(meeting));
    }

    @Test
    void testHasMeetingInTimeRange() {
        when(meetingRepository.findAllByStateIsIn(anyList())).thenReturn(Stream.of(meeting));
        val hasMeeting = meetingService.hasMeeting(room, meeting.getStartDate(), meeting.getEndDate());
        assertTrue(hasMeeting);
        verify(meetingRepository, times(1)).findAllByStateIsIn(anyList());
    }

    @Test
    void testGetMeetingsWithFilter() {
        val pageable = PageRequest.of(0, 10);
        val filter = new MeetingFilter(null, null);
        when(mongoTemplate.find(any(), eq(Meeting.class))).thenReturn(Collections.singletonList(meeting));
        val meetings = meetingService.getMeetings(hostId, filter, pageable);
        assertEquals(1, meetings.getTotalElements());
        verify(mongoTemplate, times(1)).find(any(), eq(Meeting.class));
    }

    @Test
    void testStartMeetingsTask() {
        when(meetingRepository.findAllByStateIsIn(anyList())).thenReturn(Stream.of(meetingCreated));
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meetingCreated);
        meetingService.startMeetingsTask();
        assertEquals(Meeting.State.IN_PROGRESS, meetingCreated.getState());
        verify(roomService, times(1)).updateAvailable(any(Room.class), eq(false));
        verify(meetingRepository, times(1)).save(meetingCreated);
    }
}