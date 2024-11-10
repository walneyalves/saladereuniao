package com.gitlab.walneyalves.meeting_rooms.domain.filter;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import org.springframework.lang.Nullable;

import java.time.LocalTime;

public record RoomFilter(@Nullable Integer capacity,
                         @Nullable Boolean available,
                         @Nullable LocalTime initialAvailability,
                         @Nullable LocalTime finalAvailability,
                         @Nullable Resource.Type... types) { }