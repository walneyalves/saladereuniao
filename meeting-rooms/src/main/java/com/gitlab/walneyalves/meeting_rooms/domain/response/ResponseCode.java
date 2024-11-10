package com.gitlab.walneyalves.meeting_rooms.domain.response;

import org.springframework.http.HttpStatus;

public interface ResponseCode {

    String getMessage();

    Integer getCode();

    HttpStatus getStatus();

}