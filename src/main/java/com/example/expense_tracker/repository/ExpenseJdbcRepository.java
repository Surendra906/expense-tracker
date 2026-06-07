package com.example.expense_tracker.repository;

import com.example.expense_tracker.dto.CategoryExpenseAnalytics;
import com.example.expense_tracker.dto.MonthlyExpenseAnalytics;
import com.example.expense_tracker.dto.MonthlyExpenseSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ExpenseJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExpenseJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal findTotalExpenses() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses";
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rs -> {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        });
    }

    public long findExpenseCount() {
        String sql = "SELECT COUNT(*) FROM expenses";
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rs -> {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        });
    }

    public List<CategoryExpenseAnalytics> findCategoryAnalytics() {
        String sql = "SELECT category, COALESCE(SUM(amount), 0) AS total, COUNT(*) AS count " +
                "FROM expenses GROUP BY category ORDER BY category";
        RowMapper<CategoryExpenseAnalytics> rowMapper = new RowMapper<>() {
            @Override
            public CategoryExpenseAnalytics mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CategoryExpenseAnalytics(
                        rs.getString("category"),
                        rs.getBigDecimal("total"),
                        rs.getLong("count")
                );
            }
        };
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rowMapper);
    }

    public List<MonthlyExpenseAnalytics> findMonthlyAnalytics() {
        String sql = "SELECT YEAR(expense_date) AS expense_year, MONTH(expense_date) AS expense_month, COALESCE(SUM(amount), 0) AS total, COUNT(*) AS count " +
                "FROM expenses GROUP BY YEAR(expense_date), MONTH(expense_date) ORDER BY expense_year, expense_month";
        RowMapper<MonthlyExpenseAnalytics> rowMapper = new RowMapper<>() {
            @Override
            public MonthlyExpenseAnalytics mapRow(ResultSet rs, int rowNum) throws SQLException {
                int year = rs.getInt("expense_year");
                int month = rs.getInt("expense_month");
                BigDecimal total = rs.getBigDecimal("total");
                long count = rs.getLong("count");
                String monthLabel = String.format("%04d-%02d", year, month);
                return new MonthlyExpenseAnalytics(monthLabel, total, count);
            }
        };
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rowMapper);
    }

    public Map<String, BigDecimal> findCategoryTotals() {
        String sql = "SELECT category, COALESCE(SUM(amount), 0) AS total FROM expenses GROUP BY category ORDER BY category";
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rs -> {
            Map<String, BigDecimal> totals = new LinkedHashMap<>();
            while (rs.next()) {
                totals.put(rs.getString("category"), rs.getBigDecimal("total"));
            }
            return totals;
        });
    }

    public List<MonthlyExpenseSummary> findMonthlyExpenseSummary() {
        String sql = "SELECT YEAR(expense_date) AS expense_year, MONTH(expense_date) AS expense_month, COALESCE(SUM(amount), 0) AS total " +
                "FROM expenses GROUP BY YEAR(expense_date), MONTH(expense_date) ORDER BY expense_year, expense_month";
        RowMapper<MonthlyExpenseSummary> rowMapper = new RowMapper<>() {
            @Override
            public MonthlyExpenseSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                int year = rs.getInt("expense_year");
                int month = rs.getInt("expense_month");
                BigDecimal total = rs.getBigDecimal("total");
                String monthLabel = String.format("%04d-%02d", year, month);
                return new MonthlyExpenseSummary(monthLabel, total);
            }
        };
        return jdbcTemplate.query(connection -> connection.prepareStatement(sql), rowMapper);
    }
}
