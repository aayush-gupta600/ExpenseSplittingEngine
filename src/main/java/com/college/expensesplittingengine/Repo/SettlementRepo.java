package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Group;
import com.college.expensesplittingengine.Models.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface SettlementRepo extends JpaRepository<Settlement, Long> {
    List<Settlement> findByGroup(Group group);
}