package com.gitlab.walneyalves.meeting_rooms.controller.resource;

import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceCreateRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateNameRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.request.ResourceUpdateTypeRequest;
import com.gitlab.walneyalves.meeting_rooms.domain.response.ErrorResponse;
import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Resource", description = "APIs for managing resources")
interface IResourceController {

    @PostMapping("/create")
    @Operation(summary = "Create a new resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> create(@RequestBody ResourceCreateRequest request);

    @GetMapping("/list")
    @Operation(summary = "Get a list of resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of resources retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Page<Resource>> getAll(Pageable pageable);

    @PutMapping("/update/{resourceId}/name")
    @Operation(summary = "Update the name of a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid name provided", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> updateName(@PathVariable UUID resourceId, @RequestBody ResourceUpdateNameRequest request);

    @PutMapping("/update/{resourceId}/type")
    @Operation(summary = "Update the type of a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource type updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid type provided", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> updateType(@PathVariable UUID resourceId, @RequestBody ResourceUpdateTypeRequest request);

    @DeleteMapping("/delete/{resourceId}")
    @Operation(summary = "Delete a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> delete(@PathVariable UUID resourceId);

}