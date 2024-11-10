package com.gitlab.walneyalves.meeting_rooms.domain.request;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public record MeetingCreateRequest(@NonNull String title,
                                   @Nullable String description,
                                   @NonNull LocalDateTime startDate,
                                   @NonNull LocalDateTime endDate) { }