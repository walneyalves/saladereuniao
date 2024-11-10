package com.gitlab.walneyalves.meeting_rooms.model.room;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.time.LocalTime;
import java.util.UUID;

@Document
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Room {

    public static final Integer MIN_CAPACITY = 2;

    @Id
    @Setter(AccessLevel.NONE)
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private String name;

    @NonNull
    @Builder.Default
    private Integer capacity = Room.MIN_CAPACITY;

    @NonNull
    @Builder.Default
    private Boolean available = Boolean.TRUE;

    @NonNull
    private LocalTime initialAvailability;

    @NonNull
    private LocalTime finalAvailability;

}