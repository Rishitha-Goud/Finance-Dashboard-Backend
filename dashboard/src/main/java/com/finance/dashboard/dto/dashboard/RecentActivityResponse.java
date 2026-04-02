package com.finance.dashboard.dto.dashboard;

import com.finance.dashboard.model.RecordType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RecentActivityResponse(
        Long id,
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate date,
        String notes
) {
}
