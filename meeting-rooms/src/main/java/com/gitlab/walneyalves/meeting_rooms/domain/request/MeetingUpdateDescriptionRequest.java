package com.gitlab.walneyalves.meeting_rooms.domain.request;

import org.springframework.lang.Nullable;

public record MeetingUpdateDescriptionRequest(@Nullable String description) { }