package com.gitlab.walneyalves.meeting_rooms.tests.service;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.model.room.RoomResource;
import com.gitlab.walneyalves.meeting_rooms.repository.RoomResourceRepository;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomResourceServiceTests {

    @Mock
    private ResourceService resourceService;

    @Mock
    private RoomResourceRepository roomResourceRepository;

    @InjectMocks
    private RoomResourceService roomResourceService;

    private Room room;

    private Resource resource;

    private RoomResource roomResource;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Main Room")
                .initialAvailability(LocalTime.of(8, 0,  0))
                .finalAvailability(LocalTime.of(18, 0,  0))
                .build();
        resource = Resource.builder()
                .id(UUID.randomUUID())
                .name("Projector")
                .type(Resource.Type.PROJECTOR)
                .build();
        roomResource = RoomResource.builder()
                .roomId(room.getId())
                .resourceId(resource.getId())
                .build();
    }

    @Test
    void testGetRoomResources() {
        when(roomResourceRepository.findAllByRoomId(room.getId())).thenReturn(List.of(roomResource));
        when(resourceService.get(resource.getId())).thenReturn(Optional.of(resource));
        val resources = roomResourceService.getRoomResources(room);
        assertEquals(1, resources.size());
        assertEquals(resource.getId(), resources.get(0).getId());
        verify(roomResourceRepository, times(1)).findAllByRoomId(room.getId());
        verify(resourceService, times(1)).get(resource.getId());
    }

    @Test
    void testAddResource() {
        roomResourceService.addResource(room, resource);
        verify(roomResourceRepository, times(1)).save(any(RoomResource.class));
    }

    @Test
    void testRemoveResource() {
        roomResourceService.removeResource(room, resource);
        verify(roomResourceRepository, times(1)).deleteByRoomIdAndResourceId(room.getId(), resource.getId());
    }

    @Test
    void testRemoveResources() {
        roomResourceService.removeResources(room);
        verify(roomResourceRepository, times(1)).deleteAllByRoomId(room.getId());
    }

    @Test
    void testRemoveByResource() {
        roomResourceService.removeByResource(resource);
        verify(roomResourceRepository, times(1)).deleteAllByResourceId(resource.getId());
    }
}