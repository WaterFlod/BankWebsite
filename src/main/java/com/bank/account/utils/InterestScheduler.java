package com.bank.account.utils;

import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestScheduler {

    private final AccountService accountService;

    @Scheduled(cron = "0 0 3 * * *")
    public void applyDailyInterest() {
        log.info("Запуск планового начисления процентов");
        try {
            accountService.applyInterestToSavings();
            accountService.applyInterestToCredit();
        } catch (Exception e) {
            log.error("Ошибка при начислении процентов", e);
        }
        log.info("Плановое начисление процентов завершено");
    }
}
