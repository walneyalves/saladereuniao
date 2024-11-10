package com.gitlab.walneyalves.meeting_rooms.repository;

import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface MeetingRepository extends MongoRepository<Meeting, UUID> {

    Stream<Meeting> findAllByStateIsIn(Collection<Meeting.State> states);

}