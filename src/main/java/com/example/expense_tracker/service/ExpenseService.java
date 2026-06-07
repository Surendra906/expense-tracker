package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.CategoryExpenseAnalytics;
import com.example.expense_tracker.dto.MonthlyExpenseAnalytics;
import com.example.expense_tracker.dto.MonthlyExpenseSummary;
import com.example.expense_tracker.dto.SummaryResponse;
import com.example.expense_tracker.dto.TotalExpenseAnalytics;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.exception.ExpenseNotFoundException;
import com.example.expense_tracker.repository.ExpenseJdbcRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseJdbcRepository expenseJdbcRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseJdbcRepository expenseJdbcRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseJdbcRepository = expenseJdbcRepository;
    }

    public Expense createExpense(Expense expense) {
        expense.setId(null);
        return expenseRepository.save(expense);
    }

    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense findExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = findExpenseById(id);
        existingExpense.setTitle(expense.getTitle());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setDate(expense.getDate());
        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long id) {
        Expense expense = findExpenseById(id);
        expenseRepository.delete(expense);
    }

    public Page<Expense> searchExpenses(String category, LocalDate startDate, LocalDate endDate, String title, Pageable pageable) {
        if (category == null && startDate == null && endDate == null && title == null) {
            return expenseRepository.findAll(pageable);
        }
        return expenseRepository.findByFilters(category, startDate, endDate, title, pageable);
    }

    public TotalExpenseAnalytics getTotalExpenseAnalytics() {
        return new TotalExpenseAnalytics(expenseJdbcRepository.findTotalExpenses(), expenseJdbcRepository.findExpenseCount());
    }

    public List<CategoryExpenseAnalytics> getCategoryExpenseAnalytics() {
        return expenseJdbcRepository.findCategoryAnalytics();
    }

    public List<MonthlyExpenseAnalytics> getMonthlyExpenseAnalytics() {
        return expenseJdbcRepository.findMonthlyAnalytics();
    }

    public SummaryResponse getSummary() {
        List<Expense> all = expenseRepository.findAll();
        BigDecimal total = expenseJdbcRepository.findTotalExpenses();
        Map<String, BigDecimal> byCategory = expenseJdbcRepository.findCategoryTotals();
        List<MonthlyExpenseSummary> monthlySummaries = expenseJdbcRepository.findMonthlyExpenseSummary();

        long count = all.stream().filter(e -> e.getAmount() != null).count();
        BigDecimal average = (count > 0) ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Expense highest = all.stream()
            .filter(e -> e.getAmount() != null)
            .max(Comparator.comparing(Expense::getAmount))
            .orElse(null);

        Expense lowest = all.stream()
            .filter(e -> e.getAmount() != null)
            .min(Comparator.comparing(Expense::getAmount))
            .orElse(null);

        return new SummaryResponse(total, byCategory, monthlySummaries, average, highest, lowest);
        }
}
