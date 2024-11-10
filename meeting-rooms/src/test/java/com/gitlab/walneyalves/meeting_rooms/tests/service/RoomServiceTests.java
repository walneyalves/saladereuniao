package com.gitlab.walneyalves.meeting_rooms.tests.service;

import com.gitlab.walneyalves.meeting_rooms.domain.filter.RoomFilter;
import com.gitlab.walneyalves.meeting_rooms.exception.MeetingRoomsException;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.repository.RoomRepository;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTests {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomResourceService roomResourceService;

    @InjectMocks
    private RoomService roomService;

    private Room room;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Main Room")
                .initialAvailability(LocalTime.of(8, 0))
                .finalAvailability(LocalTime.of(18, 0))
                .available(true)
                .capacity(10)
                .build();
    }

    @Test
    void testCreateRoom() {
        val createdRoom = Room
                .builder()
                .id(UUID.randomUUID())
                .name("New Room")
                .initialAvailability(LocalTime.of(8, 0))
                .finalAvailability(LocalTime.of(18, 0));
        when(roomRepository.save(any(Room.class))).thenReturn(createdRoom.build());
        assertNotNull(createdRoom);
        assertEquals("New Room", roomService.create(createdRoom).getName());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testGetRoomById() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Optional<Room> foundRoom = roomService.get(room.getId());
        assertTrue(foundRoom.isPresent());
        assertEquals(room.getId(), foundRoom.get().getId());
        verify(roomRepository, times(1)).findById(room.getId());
    }

    @Test
    void testUpdateRoomName() {
        when(roomRepository.save(room)).thenReturn(room);
        roomService.updateName(room, "Updated Room");
        assertEquals("Updated Room", room.getName());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testUpdateRoomCapacityInvalid() {
        assertThrows(MeetingRoomsException.class, () -> roomService.updateCapacity(room, -1));
    }

    @Test
    void testUpdateRoomCapacityValid() {
        when(roomRepository.save(room)).thenReturn(room);
        roomService.updateCapacity(room, 15);
        assertEquals(15, room.getCapacity());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testGetAllRoomsWithFilter() {
        val pageable = PageRequest.of(0, 5);
        val filter = new RoomFilter(null, true, LocalTime.of(8, 0), LocalTime.of(18, 0));
        when(mongoTemplate.find(any(), eq(Room.class))).thenReturn(List.of(room));
        when(roomResourceService.getRoomResources(room)).thenReturn(List.of());

        Page<Room> rooms = roomService.getAll(pageable, filter);
        assertEquals(1, rooms.getTotalElements());
        assertEquals(room, rooms.getContent().get(0));
        verify(mongoTemplate, times(1)).find(any(), eq(Room.class));
    }

    @Test
    void testIsRoomAvailable() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        assertTrue(roomService.isAvailable(room.getId()));
    }

    @Test
    void testIsTimeRangeSupported() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 9, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 25, 17, 0);
        assertTrue(roomService.isTimeRangeSupported(room, start, end));
    }

    @Test
    void testDeleteRoom() {
        roomService.delete(room);
        verify(roomRepository, times(1)).deleteById(room.getId());
    }
}