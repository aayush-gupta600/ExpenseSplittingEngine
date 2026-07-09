package com.college.expensesplittingengine.DTO.GroupDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGroupRequest {
    @NotBlank
    private String name;

    private String description;
}