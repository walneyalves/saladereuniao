package com.gitlab.walneyalves.meeting_rooms.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.walneyalves.meeting_rooms.controller.room.RoomController;
import com.gitlab.walneyalves.meeting_rooms.domain.filter.RoomFilter;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateAvailabilityPeriodRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateCapacityRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@WebMvcTest(RoomController.class)
class RoomControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomResourceService roomResourceService;

    @MockBean
    private ResourceService resourceService;

    private UUID roomId;

    private Room room;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        roomId = UUID.randomUUID();
        room = Room.builder().id(roomId).name("Meeting Room").initialAvailability(LocalTime.of(9, 0)).finalAvailability(LocalTime.of(17, 0)).capacity(10).build();
        when(roomService.isAvailable(roomId)).thenReturn(true);
    }

    @Test
    void testCreateRoom() throws Exception {
        val request = new RoomCreateRequest("Meeting Room", 4, LocalTime.of(15, 0), LocalTime.of(18, 0));
        when(roomService.isValidCapacity(request.capacity())).thenReturn(true);
        when(roomService.create(any(Room.RoomBuilder.class))).thenReturn(room);
        mockMvc.perform(post("/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Meeting Room"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void testGetRooms() throws Exception {
        val pageable = PageRequest.of(0, 10);
        val page = new PageImpl<>(List.of(room), pageable, 1);
        when(roomService.getAll(any(Pageable.class), any(RoomFilter.class))).thenReturn(page);
        mockMvc.perform(get("/room/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Meeting Room"));
    }

    @Test
    void testUpdateRoomName() throws Exception {
        val request = new RoomUpdateNameRequest("Updated Meeting Room");
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(roomService.updateName(any(Room.class), anyString())).thenReturn(room);
        mockMvc.perform(put(String.format("/room/update/%s/name", roomId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Meeting Room"));
    }

    @Test
    void testUpdateRoomCapacity() throws Exception {
        val request = new RoomUpdateCapacityRequest(15);
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(roomService.updateCapacity(any(Room.class), anyInt())).thenReturn(room);
        mockMvc.perform(put(String.format("/room/update/%s/capacity", roomId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void testUpdateRoomAvailabilityPeriod() throws Exception {
        val request = new RoomUpdateAvailabilityPeriodRequest(LocalTime.of(8, 0), LocalTime.of(18, 0));
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(roomService.updateAvailabilityPeriod(any(Room.class), eq(request.initialAvailability()), eq(request.finalAvailability()))).thenReturn(room);
        mockMvc.perform(put(String.format("/room/update/%s/availability-period", roomId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.initialAvailability[0]").value(9))
                .andExpect(jsonPath("$.initialAvailability[1]").value(0))
                .andExpect(jsonPath("$.finalAvailability[0]").value(17))
                .andExpect(jsonPath("$.finalAvailability[1]").value(0));
    }

    @Test
    void testAddResourceToRoom() throws Exception {
        val resourceId = UUID.randomUUID();
        val resource = Resource.builder().id(resourceId).name("Projector").type(Resource.Type.PROJECTOR).build();
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(resourceService.get(resourceId)).thenReturn(Optional.of(resource));
        doNothing().when(roomResourceService).addResource(any(Room.class), any(Resource.class));

        mockMvc.perform(put(String.format("/room/update/%s/resources/add/%s", roomId, resourceId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testRemoveResourceFromRoom() throws Exception {
        val resourceId = UUID.randomUUID();
        val resource = Resource.builder().id(resourceId).name("Projector").type(Resource.Type.PROJECTOR).build();
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        when(resourceService.get(resourceId)).thenReturn(Optional.of(resource));
        doNothing().when(roomResourceService).removeResource(any(Room.class), any(Resource.class));
        mockMvc.perform(put(String.format("/room/update/%s/resources/remove/%s", roomId, resourceId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRoom() throws Exception {
        when(roomService.get(roomId)).thenReturn(Optional.of(room));
        doNothing().when(roomService).delete(any(Room.class));
        mockMvc.perform(delete("/room/delete/" + roomId))
                .andExpect(status().isNoContent());
    }
}