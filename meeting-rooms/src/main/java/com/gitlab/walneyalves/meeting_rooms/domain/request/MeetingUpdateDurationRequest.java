package com.gitlab.walneyalves.meeting_rooms.domain.request;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record MeetingUpdateDurationRequest(@NonNull LocalDateTime startDate,
                                           @NonNull LocalDateTime endDate) { }