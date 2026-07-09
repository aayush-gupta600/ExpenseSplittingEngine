package com.college.expensesplittingengine.DTO.expenseDto;

import com.college.expensesplittingengine.Models.SplitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateExpenseRequest {

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private SplitType splitType;

    @NotNull
    private List<String> participants;

    private Map<String, BigDecimal> splits;
}