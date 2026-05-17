package com.example.expense_tracker.repository;

import com.example.expense_tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	    @Query("SELECT e FROM Expense e WHERE " +
		    "(:category IS NULL OR e.category = :category) AND " +
		    "(:startDate IS NULL OR e.date >= :startDate) AND " +
		    "(:endDate IS NULL OR e.date <= :endDate) AND " +
		    "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))" )
	    Page<Expense> findByFilters(@Param("category") String category,
					@Param("startDate") LocalDate startDate,
					@Param("endDate") LocalDate endDate,
					@Param("title") String title,
					Pageable pageable);
}
