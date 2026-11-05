package com.hwsinha.carbid.group;

import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<GroupResponse> createGroup(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateGroupRequest request) {
        Long userId= Long.valueOf(jwt.getSubject());
        GroupResponse body=groupService.createGroup(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/{groupId}/join")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId= Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(groupService.joinGroup(groupId,userId));
    }
}
