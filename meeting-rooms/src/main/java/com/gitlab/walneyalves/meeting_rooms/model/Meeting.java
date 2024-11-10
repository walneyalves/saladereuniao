package com.gitlab.walneyalves.meeting_rooms.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Meeting {

    @Id
    @Setter(AccessLevel.NONE)
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private UUID host;

    @NonNull
    @Setter(AccessLevel.NONE)
    private UUID roomId;

    @NonNull
    private String title;

    @Nullable
    private String description;

    @NonNull
    @Builder.Default
    private State state = State.CREATED;

    @NonNull
    private LocalDateTime startDate;

    @NonNull
    private LocalDateTime endDate;

    @NonNull
    @CreatedDate
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdDate = LocalDateTime.now();

    public enum State {
        CREATED,
        IN_PROGRESS,
        CANCELLED,
        ENDED,
    }
}