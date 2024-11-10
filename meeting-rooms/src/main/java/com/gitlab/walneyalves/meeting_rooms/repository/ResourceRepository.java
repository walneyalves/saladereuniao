package com.gitlab.walneyalves.meeting_rooms.repository;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResourceRepository extends MongoRepository<Resource, UUID> { }