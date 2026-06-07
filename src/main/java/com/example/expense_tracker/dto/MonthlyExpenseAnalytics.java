package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record MonthlyExpenseAnalytics(String month, BigDecimal totalAmount, long expenseCount) {
}
