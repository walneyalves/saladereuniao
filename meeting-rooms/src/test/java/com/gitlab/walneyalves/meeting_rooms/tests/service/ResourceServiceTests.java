package com.gitlab.walneyalves.meeting_rooms.tests.service;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.repository.ResourceRepository;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTests {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private UUID resourceId;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        resource = Resource.builder()
                .id(resourceId)
                .name("Test Resource")
                .type(Resource.Type.TV)
                .build();
    }

    @Test
    void testCreateResource() {
        val builder = Resource.builder()
                .name("New Resource")
                .type(Resource.Type.PROJECTOR);
        when(resourceRepository.save(any(Resource.class))).thenReturn(builder.build());
        val createdResource = resourceService.create(builder);
        assertNotNull(createdResource);
        assertEquals("New Resource", createdResource.getName());
        assertEquals(Resource.Type.PROJECTOR, createdResource.getType());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testGetResourceByIdFound() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        val foundResource = resourceService.get(resourceId);
        assertTrue(foundResource.isPresent());
        assertEquals(resourceId, foundResource.get().getId());
        assertEquals("Test Resource", foundResource.get().getName());
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    void testGetResourceByIdNotFound() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());
        val foundResource = resourceService.get(resourceId);
        assertFalse(foundResource.isPresent());
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    void testGetAllResources() {
        val pageable = PageRequest.of(0, 10);
        val resourcePage = new PageImpl<>(Collections.singletonList(resource));
        when(resourceRepository.findAll(pageable)).thenReturn(resourcePage);
        val resources = resourceService.getAll(pageable);
        assertEquals(1, resources.getTotalElements());
        assertEquals(resource, resources.getContent().get(0));
        verify(resourceRepository, times(1)).findAll(pageable);
    }

    @Test
    void testUpdateResourceName() {
        val newName = "Updated Resource";
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        val updatedResource = resourceService.updateName(resource, newName);
        assertEquals(newName, updatedResource.getName());
        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void testUpdateResourceType() {
        val newType = Resource.Type.NOTEBOOK;
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        val updatedResource = resourceService.updateType(resource, newType);
        assertEquals(newType, updatedResource.getType());
        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void testDeleteResource() {
        doNothing().when(resourceRepository).deleteById(resourceId);
        resourceService.delete(resource);
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }
}