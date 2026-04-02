package com.finance.dashboard.dto.dashboard;

import java.math.BigDecimal;

public record MonthlyTrendResponse(
        String month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net
) {
}
