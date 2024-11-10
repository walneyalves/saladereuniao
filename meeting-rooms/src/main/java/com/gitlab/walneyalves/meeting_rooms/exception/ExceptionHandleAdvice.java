package com.gitlab.walneyalves.meeting_rooms.exception;

import com.gitlab.walneyalves.meeting_rooms.domain.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = ExceptionAdvice.class)
public class ExceptionHandleAdvice {

    @ExceptionHandler(MeetingRoomsException.class)
    public ResponseEntity<ErrorResponse> handleMeetingException(final MeetingRoomsException exception) {
        return ResponseEntity
                .status(exception.getStatus().value())
                .body(ErrorResponse
                        .builder()
                        .status(exception.getStatus())
                        .message(exception.getMessage())
                        .code(exception.getCode())
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnmappedException(final RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Unmapped error: %s".formatted(exception.getMessage()))
                        .code(Integer.MAX_VALUE)
                        .build());
    }
}