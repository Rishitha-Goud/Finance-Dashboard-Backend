package com.finance.dashboard.config;

import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import com.finance.dashboard.model.entity.AppUser;
import com.finance.dashboard.model.entity.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            FinancialRecordRepository financialRecordRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            AppUser admin = userRepository.save(AppUser.builder()
                    .name("Alice Admin")
                    .email("admin@finance.local")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build());

            AppUser analyst = userRepository.save(AppUser.builder()
                    .name("Ben Analyst")
                    .email("analyst@finance.local")
                    .password(passwordEncoder.encode("Analyst@123"))
                    .role(Role.ANALYST)
                    .status(UserStatus.ACTIVE)
                    .build());

            userRepository.save(AppUser.builder()
                    .name("Vera Viewer")
                    .email("viewer@finance.local")
                    .password(passwordEncoder.encode("Viewer@123"))
                    .role(Role.VIEWER)
                    .status(UserStatus.ACTIVE)
                    .build());

            userRepository.save(AppUser.builder()
                    .name("Ian Inactive")
                    .email("inactive@finance.local")
                    .password(passwordEncoder.encode("Inactive@123"))
                    .role(Role.ANALYST)
                    .status(UserStatus.INACTIVE)
                    .build());

            financialRecordRepository.saveAll(List.of(
                    FinancialRecord.builder()
                            .amount(new BigDecimal("4500.00"))
                            .type(RecordType.INCOME)
                            .category("Salary")
                            .date(LocalDate.now().minusDays(10))
                            .notes("Monthly salary credit")
                            .createdBy(admin)
                            .build(),
                    FinancialRecord.builder()
                            .amount(new BigDecimal("350.25"))
                            .type(RecordType.EXPENSE)
                            .category("Groceries")
                            .date(LocalDate.now().minusDays(8))
                            .notes("Weekly supermarket spend")
                            .createdBy(admin)
                            .build(),
                    FinancialRecord.builder()
                            .amount(new BigDecimal("1200.00"))
                            .type(RecordType.INCOME)
                            .category("Freelance")
                            .date(LocalDate.now().minusDays(5))
                            .notes("Design project payment")
                            .createdBy(analyst)
                            .build(),
                    FinancialRecord.builder()
                            .amount(new BigDecimal("180.00"))
                            .type(RecordType.EXPENSE)
                            .category("Utilities")
                            .date(LocalDate.now().minusDays(4))
                            .notes("Electricity bill")
                            .createdBy(analyst)
                            .build(),
                    FinancialRecord.builder()
                            .amount(new BigDecimal("90.00"))
                            .type(RecordType.EXPENSE)
                            .category("Transport")
                            .date(LocalDate.now().minusDays(2))
                            .notes("Ride-share and fuel")
                            .createdBy(admin)
                            .build()
            ));
        };
    }
}
