package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Crossword;

import java.time.LocalDate;
import java.util.List;

public interface CrosswordService {

    String NYT_TIMEZONE = "America/New_York";

    boolean checkCurrentUserCrossword(List<String> answersToCheck);

    Crossword getClientUserCrossword();

    void configureUserCrossword(LocalDate date);

    void configureTodayUserCrossword();

    String revealLetter(int index);

    List<Integer> autocheckCurrentUserCrossword(List<String> answersToCheck);

    void saveTodaysCrossword();

}
