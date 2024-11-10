package com.gitlab.walneyalves.meeting_rooms.repository;

import com.gitlab.walneyalves.meeting_rooms.model.room.RoomResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomResourceRepository extends MongoRepository<RoomResource, UUID> {

    List<RoomResource> findAllByRoomId(UUID roomId);

    void deleteAllByRoomId(UUID roomId);

    void deleteAllByResourceId(UUID resourceId);

    void deleteByRoomIdAndResourceId(UUID roomId, UUID resourceId);

}