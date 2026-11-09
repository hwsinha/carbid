package com.hwsinha.carbid.group;

import com.hwsinha.carbid.common.PageResponse;
import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupDetailResponse;
import com.hwsinha.carbid.group.dto.GroupResponse;
import com.hwsinha.carbid.group.dto.GroupSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Create, join and browse buying groups")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "Create a buy group (buyers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Buy group is created"),
            @ApiResponse(responseCode = "403", description = "Not a buyer"),
            @ApiResponse(responseCode = "404", description = "Car or the user is not available in database")
    })
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<GroupResponse> createGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateGroupRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        GroupResponse body = groupService.createGroup(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/{groupId}/join")
    @Operation(summary = "Join a buy group (buyerss only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Joined group"),
            @ApiResponse(responseCode = "404", description = "Car or the user is not available in database"),
            @ApiResponse(responseCode = "403", description = "Not a buyer"),
            @ApiResponse(responseCode = "409", description = "Group is not open, or already a member")
    })
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(groupService.joinGroup(groupId, userId));
    }

    @GetMapping
    @Operation(summary = "List the buy groups filtering by city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List is displayed")
    })
    public PageResponse<GroupSummaryResponse> list(
            @RequestParam @NotBlank String city,
            @RequestParam(defaultValue = "OPEN") GroupStatus status,
            @ParameterObject @PageableDefault(size = 10, sort = "deadline") Pageable pageable) {
        return groupService.listGroups(city, status, pageable);
    }

    @Operation(summary = "Details of a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details shown"),
            @ApiResponse(responseCode = "404", description = "Group is not available in database")
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> details(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroup(groupId));
    }

    @GetMapping("/mine")
    @Operation(summary = "List a buyer's joined groups (buyers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details shown"),
            @ApiResponse(responseCode = "403", description = "Not a buyer")
    })
    @PreAuthorize("hasRole('BUYER')")
    public PageResponse<GroupSummaryResponse> myGroups(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @ParameterObject @PageableDefault(size = 10, sort = "deadline") Pageable pageable) {
        return groupService.listMyGroups(Long.valueOf(jwt.getSubject()), pageable);
    }
}
