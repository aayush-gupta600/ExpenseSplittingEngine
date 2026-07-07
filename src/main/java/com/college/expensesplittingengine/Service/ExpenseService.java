package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Repo.ExpenseSplitRepo;
import com.college.expensesplittingengine.Repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepo expensesRepo;

    @Autowired
    private ExpenseSplitRepo expenseSplitRepo;
}