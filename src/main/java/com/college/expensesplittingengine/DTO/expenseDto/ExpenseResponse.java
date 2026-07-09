package com.college.expensesplittingengine.DTO.expenseDto;

import com.college.expensesplittingengine.Models.SplitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;

    private String description;

    private BigDecimal amount;

    private SplitType splitType;

    private String groupName;

    private String paidBy;

    private LocalDateTime createdAt;
}