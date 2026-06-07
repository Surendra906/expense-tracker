package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record TotalExpenseAnalytics(BigDecimal totalAmount, long expenseCount) {
}
