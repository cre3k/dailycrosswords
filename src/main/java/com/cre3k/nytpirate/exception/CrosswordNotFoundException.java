package com.cre3k.nytpirate.exception;

import java.time.LocalDate;

public class CrosswordNotFoundException extends RuntimeException {

    public CrosswordNotFoundException(LocalDate date) {
        super("Crossword not found for date " + date);
    }
}
