package com.gitlab.walneyalves.meeting_rooms.controller.room;

import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateAvailabilityPeriodRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateCapacityRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.RoomUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.ErrorResponse;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.model.room.Room;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Room", description = "APIs for managing rooms")
public interface IRoomController {

    @PostMapping("/create")
    @Operation(summary = "Create a new room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> create(@RequestBody RoomCreateRequest request);

    @GetMapping("/list")
    @Operation(summary = "List rooms with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Page<Room>> getRooms(Pageable pageable,
                                        @RequestParam(required = false) @Nullable Integer capacity,
                                        @RequestParam(required = false) @Nullable Boolean available,
                                        @RequestParam(required = false) @Schema(type = "string", format = "HH:mm:ss") @Nullable LocalTime initialAvailability,
                                        @RequestParam(required = false) @Schema(type = "string", format = "HH:mm:ss") @Nullable LocalTime finalAvailability,
                                        @RequestParam(required = false) @Nullable Resource.Type... types);

    @GetMapping("/list/{roomId}/resources")
    @Operation(summary = "Get resources of a specific room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<Resource>> getRoomResources(@PathVariable UUID roomId);

    @PutMapping("/update/{roomId}/name")
    @Operation(summary = "Update the name of a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid name provided", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> updateName(@PathVariable UUID roomId, @RequestBody RoomUpdateNameRequest request);

    @PutMapping("/update/{roomId}/capacity")
    @Operation(summary = "Update the capacity of a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room capacity updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid capacity value", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> updateCapacity(@PathVariable UUID roomId, @RequestBody RoomUpdateCapacityRequest request);

    @PutMapping("/update/{roomId}/availability-period")
    @Operation(summary = "Update the availability period of a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability period updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid availability period", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> updateAvailabilityPeriod(@PathVariable UUID roomId, @RequestBody RoomUpdateAvailabilityPeriodRequest request);

    @PutMapping("/update/{roomId}/resources/add/{resourceId}")
    @Operation(summary = "Add a resource to a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource added successfully"),
            @ApiResponse(responseCode = "404", description = "Room or resource not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> addResource(@PathVariable UUID roomId, @PathVariable UUID resourceId);

    @PutMapping("/update/{roomId}/resources/remove/{resourceId}")
    @Operation(summary = "Remove a specific resource from a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource removed successfully"),
            @ApiResponse(responseCode = "404", description = "Room or resource not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Room> removeResource(@PathVariable UUID roomId, @PathVariable UUID resourceId);

    @PutMapping("/update/{roomId}/resources/remove")
    @Operation(summary = "Remove all resources from a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All resources removed successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> removeResources(@PathVariable UUID roomId);

    @DeleteMapping("/delete/{roomId}")
    @Operation(summary = "Delete a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> delete(@PathVariable UUID roomId);
}