package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Repo.ExpenseSplitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseSplitService {

    @Autowired
    private ExpenseSplitRepo expenseSplitRepo;
}