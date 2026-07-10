package com.college.expensesplittingengine.DTO.Balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedBalanceResponse {
    private String fromUser;
    private String toUser;
    private BigDecimal amount;
}