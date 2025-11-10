package com.cre3k.nytpirate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Clue {
    private Direction direction;
    private String text;
    private String label;

    public String getDisplayedText() {
        return String.format("%s. %s", label, text);
    }
}
