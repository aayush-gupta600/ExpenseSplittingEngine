package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Repo.GroupMembersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMembersService {

    @Autowired
    private GroupMembersRepo groupMembersRepo;

}