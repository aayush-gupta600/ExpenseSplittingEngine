package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.GroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMembersRepo extends JpaRepository<GroupMembers,Long> {

}
