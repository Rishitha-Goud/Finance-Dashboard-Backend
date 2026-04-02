package com.finance.dashboard.service;

import com.finance.dashboard.dto.record.FinancialRecordRequest;
import com.finance.dashboard.dto.record.FinancialRecordResponse;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.entity.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecords(RecordType type, String category, LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        Specification<FinancialRecord> specification = FinancialRecordSpecifications.withFilters(type, category, from, to);
        return financialRecordRepository.findAll(specification).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecord(Long id) {
        return toResponse(findRecord(id));
    }

    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.amount())
                .type(request.type())
                .category(request.category().trim())
                .date(request.date())
                .notes(request.notes())
                .createdBy(currentUser())
                .build();
        return toResponse(financialRecordRepository.save(record));
    }

    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = findRecord(id);
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setDate(request.date());
        record.setNotes(request.notes());
        return toResponse(financialRecordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findRecord(id);
        financialRecordRepository.delete(record);
    }

    private FinancialRecord findRecord(Long id) {
        return financialRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial record not found with id " + id));
    }

    private com.finance.dashboard.model.entity.AppUser currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getDate(),
                record.getNotes(),
                record.getCreatedBy().getEmail()
        );
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
    }
}
