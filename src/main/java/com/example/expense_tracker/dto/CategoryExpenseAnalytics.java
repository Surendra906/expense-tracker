package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record CategoryExpenseAnalytics(String category, BigDecimal totalAmount, long expenseCount) {
}
