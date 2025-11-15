package com.example.dbcarrentalsapp;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RentalScheduler {

    private static final int CHECK_INTERVAL_MINUTES = 5; // run every 5 minutes
    private final RentalDAO rentalDAO;

    public RentalScheduler(RentalDAO rentalDAO) {
        this.rentalDAO = rentalDAO;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Running rental grace period check at " + LocalDateTime.now());
                rentalDAO.applyGracePeriod(LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }
}