package com.dev.money.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.dev.money.entity.ProfileEntity;
import com.dev.money.dto.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfileService profileService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    public Map<String,Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String,Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();
        List<RecentTransactionDTO> recentTransactions = Stream.concat(latestIncomes.stream().map(income->
                        RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()),
                latestExpenses.stream().map(expense->
                        RecentTransactionDTO.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .icon(expense.getIcon())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .date(expense.getDate())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .type("expense")
                        .build()))
                .sorted((a,b)->{
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());

        returnValue.put("totalBalance",incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        returnValue.put("totalIncome",incomeService.getTotalIncomesForCurrentUser());
        returnValue.put("totalExpense",expenseService.getTotalExpensesForCurrentUser());
        returnValue.put("recent5Incomes",latestIncomes);
        returnValue.put("recent5Expenses",latestExpenses);
        returnValue.put("recentTransactions",recentTransactions);
        return returnValue;
                
    }


}
