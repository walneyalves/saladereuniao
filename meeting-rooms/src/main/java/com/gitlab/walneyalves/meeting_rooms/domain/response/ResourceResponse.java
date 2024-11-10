package com.gitlab.walneyalves.meeting_rooms.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResourceResponse implements ResponseCode {

    NOT_FOUND(14041, HttpStatus.NOT_FOUND, "Resource not found.");

    private final Integer code;

    private final HttpStatus status;

    private final String message;

}