package com.college.expensesplittingengine.Controller;

import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.DTO.Settlement.SettlementRequest;
import com.college.expensesplittingengine.Service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @PostMapping("/{groupCode}/settlements")
    public MessageResponse settleUp(
            @PathVariable String groupCode,
            @Valid @RequestBody SettlementRequest request) {

        return settlementService.settleUp(groupCode, request);
    }
}