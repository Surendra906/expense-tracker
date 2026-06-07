package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record MonthlyExpenseSummary(String month, BigDecimal total) {
}
