package com.gitlab.walneyalves.meeting_rooms.service.room;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import com.gitlab.walneyalves.meeting_rooms.model.room.RoomResource;
import com.gitlab.walneyalves.meeting_rooms.repository.RoomResourceRepository;
import com.gitlab.walneyalves.meeting_rooms.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomResourceService {

    private final ResourceService resourceService;

    private final RoomResourceRepository roomResourceRepository;

    public List<Resource> getRoomResources(@NonNull Room room) {
        return roomResourceRepository.findAllByRoomId(room.getId())
                .stream()
                .map(RoomResource::getResourceId)
                .map(resourceService::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Transactional
    public void addResource(@NonNull Room room, @NonNull Resource resource) {
        val build = RoomResource.builder()
                .roomId(room.getId())
                .resourceId(resource.getId())
                .build();
        roomResourceRepository.save(build);
    }

    @Transactional
    public void removeResource(@NonNull Room room, @NonNull Resource resource) {
        roomResourceRepository.deleteByRoomIdAndResourceId(room.getId(), resource.getId());
    }

    @Transactional
    public void removeResources(@NonNull Room room) {
        roomResourceRepository.deleteAllByRoomId(room.getId());
    }

    @Transactional
    public void removeByResource(@NonNull Resource resource) {
        roomResourceRepository.deleteAllByResourceId(resource.getId());
    }
}