package com.gitlab.walneyalves.meeting_rooms.domain.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorResponse(Integer code, HttpStatus status, String message) { }