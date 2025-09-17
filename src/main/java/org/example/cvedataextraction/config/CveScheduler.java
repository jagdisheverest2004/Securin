package org.example.cvedataextraction.config;

import org.example.cvedataextraction.service.CveIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CveScheduler {

    @Autowired
    private CveIngestionService cveIngestionService;

    // Run this task every 24 hours
    @Scheduled(fixedRate = 86400000)
    public void scheduledCveFetch() {
        System.out.println("Starting scheduled CVE data ingestion...");
        cveIngestionService.fetchAndStoreAllCves();
        System.out.println("Scheduled CVE data ingestion completed.");
    }
}