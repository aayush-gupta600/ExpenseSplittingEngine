package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Repo.SettlementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    @Autowired
    private SettlementRepo settlementsRepo;
}