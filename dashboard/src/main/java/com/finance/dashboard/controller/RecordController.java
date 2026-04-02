package com.finance.dashboard.controller;

import com.finance.dashboard.dto.record.FinancialRecordRequest;
import com.finance.dashboard.dto.record.FinancialRecordResponse;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final FinancialRecordService financialRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public List<FinancialRecordResponse> getRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return financialRecordService.getRecords(type, category, from, to);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public FinancialRecordResponse getRecord(@PathVariable Long id) {
        return financialRecordService.getRecord(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FinancialRecordResponse createRecord(@Valid @RequestBody FinancialRecordRequest request) {
        return financialRecordService.createRecord(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FinancialRecordResponse updateRecord(@PathVariable Long id, @Valid @RequestBody FinancialRecordRequest request) {
        return financialRecordService.updateRecord(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
    }
}
