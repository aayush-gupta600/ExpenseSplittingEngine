package com.college.expensesplittingengine.DTO.Balance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceNode {
    private String username;
    private BigDecimal amount;
}