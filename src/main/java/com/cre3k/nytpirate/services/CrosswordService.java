package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Crossword;

import java.util.List;

public interface CrosswordService {
    public Crossword getCurrentCrossword();

    public boolean checkCurrentCrossword(List<String> answersToCheck);

    public String revealLetter(int index);

    public List<Integer> autocheckCurrentCrossword(List<String> answersToCheck);
}
