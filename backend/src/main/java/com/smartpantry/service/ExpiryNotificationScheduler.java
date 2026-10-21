package com.smartpantry.service;


import com.smartpantry.model.PantryItem;
import com.smartpantry.repository.PantryItemRepository;
import com.smartpantry.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ExpiryNotificationScheduler {

    @Autowired
    private PantryItemRepository pantryItemRepository;

    @Autowired
    private EmailService emailService;

    // Har roz subah 9:00 baje run hoga (Cron format: sec min hr day month weekday)
    // Testing ke liye aap fixedRate ya testing cron use kar sakte hain
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndSendExpiryAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(3); // Agle 3 din me expire hone wale items

        System.out.println("Running automated daily expiry check...");

        // Database se items fetch karein jo today se lekar thresholdDate tak expire ho rahe hain
        List<PantryItem> expiringItems = pantryItemRepository.findByExpiryDateBetween(today, thresholdDate);

        for (PantryItem item : expiringItems) {
            long daysLeft = ChronoUnit.DAYS.between(today, item.getExpiryDate());

            emailService.sendExpiryAlertEmail(
                    item.getUserEmail(),
                    item.getName(),
                    item.getExpiryDate().toString(),
                    (int) daysLeft
            );
        }
    }
}