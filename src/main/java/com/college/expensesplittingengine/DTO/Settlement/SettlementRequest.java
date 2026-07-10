package com.college.expensesplittingengine.DTO.Settlement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementRequest {

    @NotBlank
    private String toUser;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}