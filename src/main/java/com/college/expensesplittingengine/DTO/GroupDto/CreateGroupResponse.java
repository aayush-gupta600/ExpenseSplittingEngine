package com.college.expensesplittingengine.DTO.GroupDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupResponse {

    private String name;
    private String groupCode;
}