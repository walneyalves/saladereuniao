package com.gitlab.walneyalves.meeting_rooms.domain.request;

import org.springframework.lang.NonNull;

public record RoomUpdateNameRequest(@NonNull String name) { }