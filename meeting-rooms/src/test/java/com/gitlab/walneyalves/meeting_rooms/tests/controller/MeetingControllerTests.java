package com.gitlab.walneyalves.meeting_rooms.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.walneyalves.meeting_rooms.configuration.TestcontainersConfiguration;
import com.gitlab.walneyalves.meeting_rooms.controller.meeting.MeetingController;
import com.gitlab.walneyalves.meeting_rooms.domain.filter.MeetingFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDescriptionRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDurationRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateTitleRequest;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting.State;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.service.MeetingService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@AutoConfigureDataMongo
@WebMvcTest(MeetingController.class)
class MeetingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private MeetingService meetingService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID meetingId;

    private UUID roomId;

    private UUID hostId;

    private Meeting meeting;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        meetingId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        hostId = UUID.randomUUID();
        meeting = Meeting.builder()
                .id(meetingId)
                .roomId(roomId)
                .host(hostId)
                .title("Team Meeting")
                .description("Monthly review")
                .startDate(LocalDateTime.of(2024, 11, 8, 10, 0))
                .endDate(LocalDateTime.of(2024, 11, 8, 11, 0))
                .state(State.CREATED)
                .build();
    }

    @Test
    void testCreateMeeting() throws Exception {
        val request = new MeetingCreateRequest(
                "Team Meeting",
                "Monthly review",
                LocalDateTime.of(2024, 11, 8, 10, 0),
                LocalDateTime.of(2024, 11, 8, 11, 0)
        );
        val room = Room.builder()
                .id(roomId)
                .name("Test Room")
                .initialAvailability(LocalTime.of(8, 0))
                .finalAvailability(LocalTime.of(18, 0))
                .capacity(4)
                .build();
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(roomService.isTimeRangeSupported(room, request.startDate(), request.endDate())).thenReturn(true);
        when(roomService.isAvailable(roomId)).thenReturn(true);
        when(meetingService.hasMeeting(room, request.startDate(), request.endDate())).thenReturn(false);
        when(meetingService.create(request, room, hostId)).thenReturn(meeting);
        mockMvc.perform(post("/meeting/create/" + roomId)
                        .header("Host-Id", hostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Monthly review"));
    }

    @Test
    void testGetMeetings() throws Exception {
        val pageable = PageRequest.of(0, 10);
        val page = new PageImpl<>(List.of(meeting), pageable, 1);
        when(meetingService.getMeetings(any(UUID.class), any(MeetingFilter.class), any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get("/meeting/list")
                .header("Host-Id", hostId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Team Meeting"));
    }

    @Test
    void testCancelMeeting() throws Exception {
        when(meetingService.get(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingService.canCancel(meeting)).thenReturn(true);
        mockMvc.perform(put("/meeting/cancel/" + meetingId)
                .header("Host-Id", hostId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateMeetingTitle() throws Exception {
        val request = new MeetingUpdateTitleRequest("Updated Meeting Title");
        when(meetingService.get(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingService.updateTitle(any(Meeting.class), anyString())).thenReturn(meeting);
        mockMvc.perform(put("/meeting/update/" + meetingId + "/title")
                .header("Host-Id", hostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Meeting"));
    }

    @Test
    void testUpdateMeetingDescription() throws Exception {
        val request = new MeetingUpdateDescriptionRequest("Updated description");
        when(meetingService.get(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingService.updateDescription(any(Meeting.class), anyString())).thenReturn(meeting);
        mockMvc.perform(put("/meeting/update/" + meetingId + "/description")
                .header("Host-Id", hostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Monthly review"));
    }

    @Test
    void testUpdateMeetingDuration() throws Exception {
        val request = new MeetingUpdateDurationRequest(
                LocalDateTime.of(2024, 11, 8, 10, 30),
                LocalDateTime.of(2024, 11, 8, 11, 30)
        );
        when(meetingService.get(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingService.updateDuration(any(Meeting.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(meeting);
        mockMvc.perform(put("/meeting/update/" + meetingId + "/duration")
                        .header("Host-Id", hostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate[0]").value(2024))
                .andExpect(jsonPath("$.startDate[1]").value(11))
                .andExpect(jsonPath("$.startDate[2]").value(8))
                .andExpect(jsonPath("$.startDate[3]").value(10))
                .andExpect(jsonPath("$.startDate[4]").value(0))
                .andExpect(jsonPath("$.endDate[0]").value(2024))
                .andExpect(jsonPath("$.endDate[1]").value(11))
                .andExpect(jsonPath("$.endDate[2]").value(8))
                .andExpect(jsonPath("$.endDate[3]").value(11))
                .andExpect(jsonPath("$.endDate[4]").value(0));
    }
}