package com.gitlab.walneyalves.meeting_rooms.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.walneyalves.meeting_rooms.controller.resource.ResourceController;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateTypeRequest;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@WebMvcTest(ResourceController.class)
class ResourceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private RoomResourceService roomResourceService;

    private UUID resourceId;

    private Resource resource;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        resourceId = UUID.randomUUID();
        resource = Resource.builder().id(resourceId).name("Projector").type(Resource.Type.PROJECTOR).build();
    }

    @Test
    void testCreateResource() throws Exception {
        val request = new ResourceCreateRequest("Projector", Resource.Type.PROJECTOR);
        when(resourceService.create(any(Resource.ResourceBuilder.class))).thenReturn(resource);
        mockMvc.perform(post("/resource/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Projector"))
                .andExpect(jsonPath("$.type").value("PROJECTOR"));
    }

    @Test
    void testGetAllResources() throws Exception {
        val pageable = PageRequest.of(0, 10);
        val page = new PageImpl<>(List.of(resource), pageable, 1);
        when(resourceService.getAll(any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get("/resource/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Projector"));
    }

    @Test
    void testUpdateResourceName() throws Exception {
        val request = new ResourceUpdateNameRequest("Updated Projector");
        when(resourceService.get(resourceId)).thenReturn(Optional.of(resource));
        when(resourceService.updateName(any(Resource.class), anyString())).thenReturn(resource);
        mockMvc.perform(put(String.format("/resource/update/%s/name", resourceId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projector"));
    }

    @Test
    void testUpdateResourceType() throws Exception {
        val request = new ResourceUpdateTypeRequest(Resource.Type.TV);
        val updatedResource = Resource.builder().id(resourceId).name("Projector").type(Resource.Type.TV).build();
        when(resourceService.get(resourceId)).thenReturn(Optional.of(resource));
        when(resourceService.updateType(any(Resource.class), eq(request.type()))).thenReturn(updatedResource);
        mockMvc.perform(put(String.format("/resource/update/%s/type", resourceId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TV"));
    }

    @Test
    void testDeleteResource() throws Exception {
        when(resourceService.get(resourceId)).thenReturn(Optional.of(resource));
        doNothing().when(roomResourceService).removeByResource(any(Resource.class));
        doNothing().when(resourceService).delete(any(Resource.class));
        mockMvc.perform(delete("/resource/delete/" + resourceId))
                .andExpect(status().isNoContent());
    }
}