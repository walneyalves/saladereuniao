package com.gitlab.walneyalves.meeting_rooms.controller.meeting;

import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDescriptionRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateDurationRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.MeetingUpdateTitleRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.ErrorResponse;
import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "Meeting", description = "APIs for managing meetings")
interface IMeetingController {

    @PostMapping("/create/{roomId}")
    @Operation(summary = "Create a new meeting in a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meeting created successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range or room is unavailable", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Meeting> create(@RequestHeader("Host-Id") UUID host,
                                   @PathVariable UUID roomId,
                                   @RequestBody MeetingCreateRequest request);

    @GetMapping("/list")
    @Operation(summary = "Get a list of meetings with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of meetings retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Page<Meeting>> getMeetings(Pageable pageable,
                                              @RequestHeader("Host-Id") UUID host,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable LocalDateTime startDate,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable LocalDateTime endDate,
                                              @RequestParam(required = false) @Nullable Meeting.State... states);

    @PutMapping("/cancel/{meetingId}")
    @Operation(summary = "Cancel a specific meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meeting canceled successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges to cancel the meeting", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid state for cancellation", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> cancel(@RequestHeader("Host-Id") UUID host,
                             @PathVariable UUID meetingId);

    @PutMapping("/update/{meetingId}/title")
    @Operation(summary = "Update the title of a meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting title updated successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges to update the title", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Meeting> updateTitle(@RequestHeader("Host-Id") UUID host,
                                        @PathVariable UUID meetingId,
                                        @RequestBody MeetingUpdateTitleRequest request);

    @PutMapping("/update/{meetingId}/description")
    @Operation(summary = "Update the description of a meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting description updated successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges to update the description", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Meeting> updateDescription(@RequestHeader("Host-Id") UUID host,
                                              @PathVariable UUID meetingId,
                                              @RequestBody MeetingUpdateDescriptionRequest request);

    @PutMapping("/update/{meetingId}/duration")
    @Operation(summary = "Update the duration of a meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting duration updated successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges to update the duration", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid state for updating duration", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Meeting> updateDuration(@RequestHeader("Host-Id") UUID host,
                                           @PathVariable UUID meetingId,
                                           @RequestBody MeetingUpdateDurationRequest request);
}