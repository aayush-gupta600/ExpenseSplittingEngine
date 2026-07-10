package com.college.expensesplittingengine.Service;

import com.college.expensesplittingengine.DTO.Balance.BalanceResponse;
import com.college.expensesplittingengine.Exceptions.ResourceNotFoundException;
import com.college.expensesplittingengine.Models.ExpenseSplit;
import com.college.expensesplittingengine.Models.Expenses;
import com.college.expensesplittingengine.Models.Group;
import com.college.expensesplittingengine.Repo.ExpenseRepo;
import com.college.expensesplittingengine.Repo.ExpenseSplitRepo;
import com.college.expensesplittingengine.Repo.GroupRepo;
import com.college.expensesplittingengine.DTO.Balance.BalanceNode;
import com.college.expensesplittingengine.DTO.Balance.SimplifiedBalanceResponse;
import com.college.expensesplittingengine.Repo.SettlementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import com.college.expensesplittingengine.Models.Settlement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceService {

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private ExpenseSplitRepo expenseSplitRepo;

    @Autowired
    private SettlementRepo settlementRepo;

    public List<BalanceResponse> getBalances(String groupCode) {

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        List<Expenses> expenses = expenseRepo.findByGroup(group);

        Map<String, BigDecimal> balanceMap = new HashMap<>();

        for (Expenses expense : expenses) {

            String paidBy = expense.getPaidBy().getUsername();

            List<ExpenseSplit> splits = expenseSplitRepo.findByExpense(expense);

            for (ExpenseSplit split : splits) {

                String owes = split.getUser().getUsername();

                if (owes.equals(paidBy)) {
                    continue;
                }

                String key = owes + "->" + paidBy;

                balanceMap.put(
                        key,
                        balanceMap.getOrDefault(key, BigDecimal.ZERO)
                                .add(split.getAmountOwed()));
            }
        }

        List<Settlement> settlements = settlementRepo.findByGroup(group);

        for (Settlement settlement : settlements) {

            String key = STR."\{settlement.getFromUser().getUsername()}->\{settlement.getToUser().getUsername()}";

            if (!balanceMap.containsKey(key)) {
                continue;
            }

            BigDecimal remaining = balanceMap.get(key)
                    .subtract(settlement.getAmount());

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                balanceMap.remove(key);
            } else {
                balanceMap.put(key, remaining);
            }
        }

        List<BalanceResponse> balances = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : balanceMap.entrySet()) {

            String[] users = entry.getKey().split("->");

            balances.add(
                    new BalanceResponse(
                            users[0],
                            users[1],
                            entry.getValue()
                    )
            );
        }

        return balances;
    }
    public List<SimplifiedBalanceResponse> simplifyBalances(String groupCode) {

        Group group = groupRepo.findByGroupCode(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group",
                                "groupCode",
                                groupCode));

        List<Expenses> expenses = expenseRepo.findByGroup(group);

        Map<String, BigDecimal> netBalance = new HashMap<>();

        for (Expenses expense : expenses) {

            String paidBy = expense.getPaidBy().getUsername();

            netBalance.put(
                    paidBy,
                    netBalance.getOrDefault(paidBy, BigDecimal.ZERO)
                            .add(expense.getAmount()));

            List<ExpenseSplit> splits = expenseSplitRepo.findByExpense(expense);

            for (ExpenseSplit split : splits) {

                String user = split.getUser().getUsername();

                netBalance.put(
                        user,
                        netBalance.getOrDefault(user, BigDecimal.ZERO)
                                .subtract(split.getAmountOwed()));
            }
        }

        List<Settlement> settlements = settlementRepo.findByGroup(group);

        for (Settlement settlement : settlements) {

            String from = settlement.getFromUser().getUsername();
            String to = settlement.getToUser().getUsername();

            netBalance.put(
                    from,
                    netBalance.getOrDefault(from, BigDecimal.ZERO)
                            .add(settlement.getAmount()));

            netBalance.put(
                    to,
                    netBalance.getOrDefault(to, BigDecimal.ZERO)
                            .subtract(settlement.getAmount()));
        }

        Queue<BalanceNode> creditors = new LinkedList<>();
        Queue<BalanceNode> debtors = new LinkedList<>();

        for (Map.Entry<String, BigDecimal> entry : netBalance.entrySet()) {

            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {

                creditors.offer(
                        new BalanceNode(
                                entry.getKey(),
                                entry.getValue()
                        )
                );

            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {

                debtors.offer(
                        new BalanceNode(
                                entry.getKey(),
                                entry.getValue().abs()
                        )
                );
            }
        }
        List<SimplifiedBalanceResponse> answer = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            BalanceNode creditor = creditors.poll();
            BalanceNode debtor = debtors.poll();
            BigDecimal settled =
                    creditor.getAmount().min(debtor.getAmount());
            answer.add(
                    new SimplifiedBalanceResponse(
                            debtor.getUsername(),
                            creditor.getUsername(),
                            settled
                    )
            );
            creditor.setAmount(
                    creditor.getAmount().subtract(settled));

            debtor.setAmount(
                    debtor.getAmount().subtract(settled));

            if (creditor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                creditors.offer(creditor);
            }

            if (debtor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                debtors.offer(debtor);
            }
        }
        return answer;
    }
}