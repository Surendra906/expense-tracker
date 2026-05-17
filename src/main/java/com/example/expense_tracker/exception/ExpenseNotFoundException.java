package com.example.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExpenseNotFoundException extends ResponseStatusException {

    public ExpenseNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Expense not found with id: " + id);
    }
}
