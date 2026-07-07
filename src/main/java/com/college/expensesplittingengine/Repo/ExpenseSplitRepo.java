package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseSplitRepo extends JpaRepository<ExpenseSplit, Long> {
}
