package com.college.expensesplittingengine.DTO.GroupDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinGroupRequest {

    @NotBlank
    private String groupCode;
}