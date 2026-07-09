package com.college.expensesplittingengine.DTO.GroupDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {

    private Long id;

    private String name;

    private String description;

    private String groupCode;

    private String createdBy;

    private LocalDateTime createdAt;
}