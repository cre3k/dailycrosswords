package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Crossword;

import java.time.LocalDate;
import java.util.List;

public interface CrosswordService {

    public boolean checkCurrentUserCrossword(List<String> answersToCheck);

    Crossword getClientUserCrossword();

    void configureUserCrossword(LocalDate date);

    void configureTodayUserCrossword();

    public String revealLetter(int index);

    public List<Integer> autocheckCurrentUserCrossword(List<String> answersToCheck);

    public void saveTodaysCrossword();

}
