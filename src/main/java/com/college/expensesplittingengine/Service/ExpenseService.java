package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.Auth.MessageResponse;
import com.college.expensesplittingengine.DTO.expenseDto.CreateExpenseRequest;
import com.college.expensesplittingengine.DTO.expenseDto.ExpensePageResponse;
import com.college.expensesplittingengine.DTO.expenseDto.ExpenseResponse;
import com.college.expensesplittingengine.Exceptions.ResourceNotFoundException;
import com.college.expensesplittingengine.Models.*;
import com.college.expensesplittingengine.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private ExpenseSplitRepo expenseSplitRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GroupMembersRepo groupMembersRepo;

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

    private ExpenseResponse mapToResponse(Expenses expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getSplitType(),
                expense.getGroup().getName(),
                expense.getPaidBy().getUsername(),
                expense.getCreatedAt()
        );
    }

    @Transactional
    public MessageResponse addExpense(String groupCode,
                                      CreateExpenseRequest request) {

        User paidBy = getCurrentUser();

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        validateMember(group, paidBy);

        Expenses expense = new Expenses();

        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setSplitType(request.getSplitType());
        expense.setGroup(group);
        expense.setPaidBy(paidBy);

        expense = expenseRepo.save(expense);

        switch (request.getSplitType()) {

            case EQUAL ->
                    equalSplit(expense, request.getParticipants());

            case EXACT ->
                    exactSplit(expense, request.getSplits());

            case PERCENTAGE ->
                    percentageSplit(expense, request.getSplits());
        }

        return new MessageResponse("Expense added successfully");
    }

    private void equalSplit(Expenses expense,
                            List<String> participants) {

        BigDecimal share = expense.getAmount()
                .divide(
                        BigDecimal.valueOf(participants.size()),
                        2,
                        RoundingMode.HALF_UP);

        for (String username : participants) {

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    "username",
                                    username));

            validateMember(expense.getGroup(), user);

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(share);

            expenseSplitRepo.save(split);
        }
    }
    private void exactSplit(Expenses expense,
                            Map<String, BigDecimal> splits) {

        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal amount : splits.values()) {
            total = total.add(amount);
        }

        if (total.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("Split amount does not match total expense");
        }

        for (Map.Entry<String, BigDecimal> entry : splits.entrySet()) {

            User user = userRepo.findByUsername(entry.getKey())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    "username",
                                    entry.getKey()));

            validateMember(expense.getGroup(), user);

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(entry.getValue());

            expenseSplitRepo.save(split);
        }
    }

    private void percentageSplit(Expenses expense,
                                 Map<String, BigDecimal> splits) {

        BigDecimal totalPercentage = BigDecimal.ZERO;

        for (BigDecimal percentage : splits.values()) {
            totalPercentage = totalPercentage.add(percentage);
        }

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new RuntimeException("Percentage should be exactly 100");
        }

        for (Map.Entry<String, BigDecimal> entry : splits.entrySet()) {

            User user = userRepo.findByUsername(entry.getKey())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    "username",
                                    entry.getKey()));

            validateMember(expense.getGroup(), user);

            BigDecimal amount = expense.getAmount()
                    .multiply(entry.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            ExpenseSplit split = new ExpenseSplit();

            split.setExpense(expense);
            split.setUser(user);
            split.setPercentage(entry.getValue());
            split.setAmountOwed(amount);

            expenseSplitRepo.save(split);
        }
    }

    public ExpensePageResponse getGroupExpenses(String groupCode,
                                                int pageNo,
                                                int pageSize,
                                                String sortBy,
                                                String sortDir) {

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        User user = getCurrentUser();

        validateMember(group, user);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Expenses> page = expenseRepo.findByGroup(group, pageable);

        List<ExpenseResponse> expenseResponses = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
        ExpensePageResponse response = new ExpensePageResponse();

        response.setContent(expenseResponses);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }
    @Transactional
    public MessageResponse deleteExpense(Long expenseId) {
        Expenses expense = expenseRepo.findById(expenseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense",
                                "expenseId",
                                expenseId));
        User user = getCurrentUser();

        if (!expense.getPaidBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the payer can delete this expense");
        }
        expenseSplitRepo.deleteAll(expenseSplitRepo.findByExpense(expense));
        expenseRepo.delete(expense);
        return new MessageResponse("Expense deleted successfully");
    }
}