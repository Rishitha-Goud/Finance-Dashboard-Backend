package com.finance.dashboard.dto.dashboard;

import java.math.BigDecimal;

public record CategoryTotalResponse(
        String category,
        BigDecimal total
) {
}
