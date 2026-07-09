package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Expenses;
import com.college.expensesplittingengine.Models.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepo extends JpaRepository<Expenses, Long> {

    Page<Expenses> findByGroup(Group group, Pageable pageable);

}