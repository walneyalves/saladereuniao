package com.gitlab.walneyalves.meeting_rooms.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoomResponse implements ResponseCode {

    NOT_FOUND(24041, HttpStatus.NOT_FOUND, "Room not found."),
    INVALID_CAPACITY(24002, HttpStatus.BAD_REQUEST, "Selected capacity is less than the minimum capacity allowed or is already defined in this value."),
    INVALID_AVAILABILITY_PERIOD(24003, HttpStatus.BAD_REQUEST, "Selected availability period is invalid (final time can not less than initial time)."),
    UNAVAILABLE(24004, HttpStatus.BAD_REQUEST, "Can not perform this action while unavailable.");

    private final Integer code;

    private final HttpStatus status;

    private final String message;

}