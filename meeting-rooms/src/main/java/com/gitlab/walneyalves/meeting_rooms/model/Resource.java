package com.gitlab.walneyalves.meeting_rooms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Document
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Resource {

    @Id
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private String name;

    @NonNull
    private Type type;

    public enum Type {
        PROJECTOR,
        TV,
        WHITEBOARD,
        CHART,
        MICROPHONE,
        SPEAKER,
        LASER_POINTER,
        TABLET,
        NOTEBOOK,
    }
}