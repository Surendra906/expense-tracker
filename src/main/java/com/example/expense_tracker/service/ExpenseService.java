package com.example.expense_tracker.service;

import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.exception.ExpenseNotFoundException;
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

import com.example.expense_tracker.dto.SummaryResponse;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
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

        public SummaryResponse getSummary() {
        List<Expense> all = expenseRepository.findAll();
        BigDecimal total = all.stream()
            .map(Expense::getAmount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byCategory = all.stream()
            .filter(e -> e.getCategory() != null && e.getAmount() != null)
            .collect(Collectors.groupingBy(Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

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

        return new SummaryResponse(total, byCategory, average, highest, lowest);
        }
}
