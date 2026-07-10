package com.college.expensesplittingengine.Controller;

import com.college.expensesplittingengine.DTO.Balance.BalanceResponse;
import com.college.expensesplittingengine.DTO.Balance.SimplifiedBalanceResponse;
import com.college.expensesplittingengine.Service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{groupCode}/balances")
    public List<BalanceResponse> getBalances(
            @PathVariable String groupCode) {

        return balanceService.getBalances(groupCode);
    }
    @GetMapping("/{groupCode}/simplified-balances")
    public List<SimplifiedBalanceResponse> getSimplifiedBalances(
            @PathVariable String groupCode) {

        return balanceService.simplifyBalances(groupCode);
    }
}