package com.finance.dashboard.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        List<CategoryTotalResponse> categoryTotals,
        List<MonthlyTrendResponse> monthlyTrends,
        List<RecentActivityResponse> recentActivity
) {
}
