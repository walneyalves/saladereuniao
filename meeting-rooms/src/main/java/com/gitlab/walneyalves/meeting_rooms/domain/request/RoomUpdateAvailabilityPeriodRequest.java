package com.gitlab.walneyalves.meeting_rooms.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.time.LocalTime;

public record RoomUpdateAvailabilityPeriodRequest(@NonNull @JsonFormat(pattern = "HH:mm:ss") @Schema(type = "string", format = "HH:mm:ss") LocalTime initialAvailability,
                                                  @NonNull @JsonFormat(pattern = "HH:mm:ss") @Schema(type = "string", format = "HH:mm:ss") LocalTime finalAvailability) { }