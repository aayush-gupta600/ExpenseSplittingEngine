package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Group;
import com.college.expensesplittingengine.Models.GroupMembers;
import com.college.expensesplittingengine.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMembersRepo extends JpaRepository<GroupMembers, Long> {
    boolean existsByGroupAndUser(Group group, User user);
    List<GroupMembers> findByUser(User user);
    Page<GroupMembers> findByUser(User user, Pageable pageable);
}