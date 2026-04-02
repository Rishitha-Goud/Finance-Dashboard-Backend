package com.finance.dashboard.service;

import com.finance.dashboard.dto.dashboard.CategoryTotalResponse;
import com.finance.dashboard.dto.dashboard.DashboardSummaryResponse;
import com.finance.dashboard.dto.dashboard.MonthlyTrendResponse;
import com.finance.dashboard.dto.dashboard.RecentActivityResponse;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.entity.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardSummaryResponse getSummary(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        List<FinancialRecord> records = financialRecordRepository.findAll(
                FinancialRecordSpecifications.withFilters(null, null, from, to)
        );

        BigDecimal totalIncome = sumByType(records, RecordType.INCOME);
        BigDecimal totalExpenses = sumByType(records, RecordType.EXPENSE);

        return new DashboardSummaryResponse(
                from,
                to,
                totalIncome,
                totalExpenses,
                totalIncome.subtract(totalExpenses),
                buildCategoryTotals(records),
                buildMonthlyTrends(records),
                buildRecentActivity(records)
        );
    }

    private List<CategoryTotalResponse> buildCategoryTotals(List<FinancialRecord> records) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        records.stream()
                .filter(record -> record.getType() == RecordType.EXPENSE)
                .sorted(Comparator.comparing(FinancialRecord::getDate))
                .forEach(record -> totals.merge(record.getCategory(), record.getAmount(), BigDecimal::add));

        return totals.entrySet().stream()
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<MonthlyTrendResponse> buildMonthlyTrends(List<FinancialRecord> records) {
        Map<YearMonth, List<FinancialRecord>> grouped = new LinkedHashMap<>();
        records.stream()
                .sorted(Comparator.comparing(FinancialRecord::getDate))
                .forEach(record -> grouped.computeIfAbsent(YearMonth.from(record.getDate()), unused -> new ArrayList<>()).add(record));

        return grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal income = sumByType(entry.getValue(), RecordType.INCOME);
                    BigDecimal expense = sumByType(entry.getValue(), RecordType.EXPENSE);
                    return new MonthlyTrendResponse(
                            entry.getKey().toString(),
                            income,
                            expense,
                            income.subtract(expense)
                    );
                })
                .toList();
    }

    private List<RecentActivityResponse> buildRecentActivity(List<FinancialRecord> records) {
        return records.stream()
                .sorted(Comparator.comparing(FinancialRecord::getDate).reversed())
                .limit(5)
                .map(record -> new RecentActivityResponse(
                        record.getId(),
                        record.getAmount(),
                        record.getType(),
                        record.getCategory(),
                        record.getDate(),
                        record.getNotes()
                ))
                .toList();
    }

    private BigDecimal sumByType(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
    }
}
