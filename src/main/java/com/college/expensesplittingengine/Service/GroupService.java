package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.DTO.GroupDto.CreateGroupRequest;
import com.college.expensesplittingengine.DTO.GroupDto.CreateGroupResponse;
import com.college.expensesplittingengine.DTO.GroupDto.GroupPageResponse;
import com.college.expensesplittingengine.DTO.GroupDto.GroupResponse;
import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.Exceptions.ResourceNotFoundException;
import com.college.expensesplittingengine.Models.Group;
import com.college.expensesplittingengine.Models.GroupMembers;
import com.college.expensesplittingengine.Models.User;
import com.college.expensesplittingengine.Repo.GroupMembersRepo;
import com.college.expensesplittingengine.Repo.GroupRepo;
import com.college.expensesplittingengine.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GroupMembersRepo groupMembersRepo;

    private User getCurrentUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "username",
                                username));
    }

    private String generateGroupCode() {

        String code;

        do {
            code = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (groupRepo.existsByGroupCode(code));

        return code;
    }

    private GroupResponse mapToResponse(Group group) {

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getGroupCode(),
                group.getCreatedBy().getUsername(),
                group.getCreatedAt()
        );
    }

    public CreateGroupResponse createGroup(CreateGroupRequest request) {

        User user = getCurrentUser();

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(user);
        group.setGroupCode(generateGroupCode());

        group = groupRepo.save(group);

        GroupMembers member = new GroupMembers();
        member.setGroup(group);
        member.setUser(user);

        groupMembersRepo.save(member);

        return new CreateGroupResponse(
                group.getName(),
                group.getGroupCode()
        );
    }

    public MessageResponse joinGroup(String groupCode) {

        User user = getCurrentUser();

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        if (groupMembersRepo.existsByGroupAndUser(group, user)) {
            return new MessageResponse("Already a member of this group");
        }

        GroupMembers member = new GroupMembers();
        member.setGroup(group);
        member.setUser(user);

        groupMembersRepo.save(member);

        return new MessageResponse("Joined successfully");
    }
    public GroupPageResponse getMyGroups(int pageNo,
                                         int pageSize,
                                         String sortBy,
                                         String sortDir) {

        User user = getCurrentUser();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<GroupMembers> page = groupMembersRepo.findByUser(user, pageable);

        List<GroupResponse> groups = page.getContent()
                .stream()
                .map(GroupMembers::getGroup)
                .map(this::mapToResponse)
                .toList();

        GroupPageResponse response = new GroupPageResponse();

        response.setContent(groups);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }

    public MessageResponse deleteGroup(Long groupId) {

        User user = getCurrentUser();

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupId",
                                groupId));

        if (!group.getCreatedBy().getId().equals(user.getId())) {
            return new MessageResponse("Only the group creator can delete the group");
        }

        groupRepo.delete(group);

        return new MessageResponse("Group deleted successfully");
    }
}