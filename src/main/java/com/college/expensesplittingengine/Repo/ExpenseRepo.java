package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepo extends JpaRepository<Expenses, Long> {
}
