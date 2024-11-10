package com.gitlab.walneyalves.meeting_rooms.controller.resource;

import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateTypeRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.ResourceResponse;
import com.gitlab.walneyalves.meeting_rooms.exception.ExceptionAdvice;
import com.gitlab.walneyalves.meeting_rooms.exception.MeetingRoomsException;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import com.gitlab.walneyalves.meeting_rooms.service.room.RoomResourceService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@ExceptionAdvice
@RestController
@RequestMapping("/resource")
public class ResourceController implements IResourceController {

    private final ResourceService resourceService;
    private final RoomResourceService roomResourceService;

    @Override
    public ResponseEntity<Resource> create(ResourceCreateRequest request) {
        val resource = resourceService.create(Resource.builder()
                .name(request.name())
                .type(request.type()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Override
    public ResponseEntity<Page<Resource>> getAll(Pageable pageable) {
        return ResponseEntity.ok(resourceService.getAll(pageable));
    }

    @Override
    public ResponseEntity<Resource> updateName(UUID resourceId, ResourceUpdateNameRequest request) {
        val resource = resourceService.get(resourceId)
                .orElseThrow(() -> new MeetingRoomsException(ResourceResponse.NOT_FOUND));
        return ResponseEntity.status(HttpStatus.OK).body(resourceService.updateName(resource, request.name()));
    }

    @Override
    public ResponseEntity<Resource> updateType(UUID resourceId, ResourceUpdateTypeRequest request) {
        val resource = resourceService.get(resourceId)
                .orElseThrow(() -> new MeetingRoomsException(ResourceResponse.NOT_FOUND));
        return ResponseEntity.status(HttpStatus.OK).body(resourceService.updateType(resource, request.type()));
    }

    @Override
    public ResponseEntity<?> delete(UUID resourceId) {
        val resource = resourceService.get(resourceId)
                .orElseThrow(() -> new MeetingRoomsException(ResourceResponse.NOT_FOUND));
        roomResourceService.removeByResource(resource);
        resourceService.delete(resource);
        return ResponseEntity.noContent().build();
    }
}