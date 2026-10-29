package com.hwsinha.carbid.group;

import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupResponse;
import com.hwsinha.carbid.group.dto.JoinGroupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {
        GroupResponse body=groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody JoinGroupRequest request) {
        GroupResponse body=groupService.joinGroup(groupId,request);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }


}
