package com.college.expensesplittingengine.Controller;

import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.DTO.expenseDto.CreateExpenseRequest;
import com.college.expensesplittingengine.DTO.expenseDto.ExpensePageResponse;
import com.college.expensesplittingengine.Service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;
    @PostMapping("/groups/{groupCode}/expenses")
    public MessageResponse addExpense(
            @PathVariable String groupCode,
            @Valid @RequestBody CreateExpenseRequest request) {

        return expenseService.addExpense(groupCode, request);
    }
    @GetMapping("/groups/{groupCode}/expenses")
    public ExpensePageResponse getGroupExpenses(
            @PathVariable String groupCode,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return expenseService.getGroupExpenses(
                groupCode,
                pageNo,
                pageSize,
                sortBy,
                sortDir
        );
    }
    @DeleteMapping("/expenses/{expenseId}")
    public MessageResponse deleteExpense(@PathVariable Long expenseId) {
        return expenseService.deleteExpense(expenseId);
    }
}