package com.gitlab.walneyalves.meeting_rooms.domain.request;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import org.springframework.lang.NonNull;

public record ResourceCreateRequest(@NonNull String name, @NonNull Resource.Type type) { }