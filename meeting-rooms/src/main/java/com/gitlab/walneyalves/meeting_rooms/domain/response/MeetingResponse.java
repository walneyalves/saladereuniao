package com.gitlab.walneyalves.meeting_rooms.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MeetingResponse implements ResponseCode {

    NOT_FOUND(34041, HttpStatus.NOT_FOUND, "Meeting not found."),
    INSUFFICIENT_PRIVILEGES(34011, HttpStatus.FORBIDDEN, "Insufficient privileges to perform this action on this meeting."),
    INVALID_TIME_RANGE(34002, HttpStatus.BAD_REQUEST, "Invalid time range."),
    INVALID_STATE(34001, HttpStatus.BAD_REQUEST, "Can not perform this action while meeting is in this state.");

    private final Integer code;

    private final HttpStatus status;

    private final String message;

}