package com.cre3k.nytpirate.scheduler;

import com.cre3k.nytpirate.services.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrosswordLoader {

    @Autowired
    CrosswordService crosswordService;

    @Scheduled(cron = "0 5 0 * * *", zone = CrosswordService.NYT_TIMEZONE)
    public void loadDailyCrossword() {
        crosswordService.saveTodaysCrossword();
    }

}