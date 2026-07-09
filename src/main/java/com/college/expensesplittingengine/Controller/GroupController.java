package com.college.expensesplittingengine.Controller;

import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.DTO.GroupDto.CreateGroupRequest;
import com.college.expensesplittingengine.DTO.GroupDto.CreateGroupResponse;
import com.college.expensesplittingengine.DTO.GroupDto.GroupPageResponse;
import com.college.expensesplittingengine.DTO.GroupDto.JoinGroupRequest;
import com.college.expensesplittingengine.Service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @PostMapping
    public CreateGroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @PostMapping("/join")
    public MessageResponse joinGroup(@Valid @RequestBody JoinGroupRequest request) {
        return groupService.joinGroup(request.getGroupCode());
    }

    @GetMapping
    public GroupPageResponse getMyGroups(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return groupService.getMyGroups(pageNo, pageSize, sortBy, sortDir);
    }

    @DeleteMapping("/{groupId}")
    public MessageResponse deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }
}