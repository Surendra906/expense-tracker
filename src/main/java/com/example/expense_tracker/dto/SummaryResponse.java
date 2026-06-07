package com.example.expense_tracker.dto;

import com.example.expense_tracker.dto.MonthlyExpenseSummary;
import com.example.expense_tracker.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private BigDecimal totalExpenses;
    private Map<String, BigDecimal> categoryTotals;
    private List<MonthlyExpenseSummary> monthlyExpenseSummaries;
    private BigDecimal averageExpense;
    private Expense highestExpense;
    private Expense lowestExpense;
}
