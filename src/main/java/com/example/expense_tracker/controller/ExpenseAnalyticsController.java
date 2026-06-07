package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.CategoryExpenseAnalytics;
import com.example.expense_tracker.dto.MonthlyExpenseAnalytics;
import com.example.expense_tracker.dto.TotalExpenseAnalytics;
import com.example.expense_tracker.service.ExpenseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expenses/analytics")
public class ExpenseAnalyticsController {

    private final ExpenseService expenseService;

    public ExpenseAnalyticsController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/total")
    public TotalExpenseAnalytics getTotalAnalytics() {
        return expenseService.getTotalExpenseAnalytics();
    }

    @GetMapping("/category")
    public List<CategoryExpenseAnalytics> getCategoryAnalytics() {
        return expenseService.getCategoryExpenseAnalytics();
    }

    @GetMapping("/monthly")
    public List<MonthlyExpenseAnalytics> getMonthlyAnalytics() {
        return expenseService.getMonthlyExpenseAnalytics();
    }
}
