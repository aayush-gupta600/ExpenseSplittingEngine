package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.DTO.Balance.SimplifiedBalanceResponse;
import com.college.expensesplittingengine.DTO.Settlement.SettlementRequest;
import com.college.expensesplittingengine.Exceptions.ResourceNotFoundException;
import com.college.expensesplittingengine.Models.Group;
import com.college.expensesplittingengine.Models.Settlement;
import com.college.expensesplittingengine.Models.User;
import com.college.expensesplittingengine.Repo.GroupMembersRepo;
import com.college.expensesplittingengine.Repo.GroupRepo;
import com.college.expensesplittingengine.Repo.SettlementRepo;
import com.college.expensesplittingengine.Repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SettlementService {

    @Autowired
    private SettlementRepo settlementRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GroupMembersRepo groupMembersRepo;

    @Autowired
    private BalanceService balanceService;

    private User getCurrentUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "username",
                                username));
    }

    private void validateMember(Group group, User user) {

        if (!groupMembersRepo.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is not a member of this group");
        }
    }

    @Transactional
    public MessageResponse settleUp(String groupCode,
                                    SettlementRequest request) {

        User fromUser = getCurrentUser();

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        validateMember(group, fromUser);

        User toUser = userRepo.findByUsername(request.getToUser())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "username",
                                request.getToUser()));

        validateMember(group, toUser);
        if (fromUser.getId().equals(toUser.getId())) {
            throw new RuntimeException("You cannot settle with yourself");
        }
        Settlement settlement = new Settlement();

        settlement.setGroup(group);
        settlement.setFromUser(fromUser);
        settlement.setToUser(toUser);
        settlement.setAmount(request.getAmount());

        settlementRepo.save(settlement);
        return new MessageResponse("Settlement recorded successfully");
    }
    private void validateSettlement(String groupCode,
                                    User fromUser,
                                    User toUser,
                                    BigDecimal amount) {

        List<SimplifiedBalanceResponse> balances =
                balanceService.simplifyBalances(groupCode);

        for (SimplifiedBalanceResponse balance : balances) {

            if (balance.getFromUser().equals(fromUser.getUsername())
                    && balance.getToUser().equals(toUser.getUsername())) {
                if (amount.compareTo(balance.getAmount()) > 0) {
                    throw new RuntimeException(
                            "Settlement amount exceeds outstanding balance");
                }

                return;
            }
        }
        throw new RuntimeException("No outstanding balance found between these users");
    }
}