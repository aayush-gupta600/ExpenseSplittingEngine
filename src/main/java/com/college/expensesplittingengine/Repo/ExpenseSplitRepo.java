package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.ExpenseSplit;
import com.college.expensesplittingengine.Models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepo extends JpaRepository<ExpenseSplit, Long> {

    List<ExpenseSplit> findByExpense(Expenses expense);

}