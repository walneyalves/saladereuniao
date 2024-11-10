package com.gitlab.walneyalves.meeting_rooms.exception;

import com.gitlab.walneyalves.meeting_rooms.domain.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MeetingRoomsException extends RuntimeException {

    private final HttpStatus status;

    private final Integer code;

    public MeetingRoomsException(ResponseCode code) {
        super(code.getMessage());
        this.code = code.getCode();
        this.status = code.getStatus();
    }
}