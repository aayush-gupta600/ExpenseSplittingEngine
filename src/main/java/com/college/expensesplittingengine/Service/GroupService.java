package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Repo.GroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    @Autowired
    private GroupRepo groupRepo;
}
