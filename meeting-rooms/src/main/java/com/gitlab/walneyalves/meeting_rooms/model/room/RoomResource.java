package com.gitlab.walneyalves.meeting_rooms.model.room;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Document
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RoomResource {

    @Id
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NonNull
    private UUID roomId;

    @NonNull
    private UUID resourceId;

}