package com.hwsinha.carbid.group;

import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
