package com.cre3k.nytpirate.model.dto;

import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Direction;

public record ClueDto(String label, Direction direction, String text) {
    public static ClueDto from(Clue clue) {
        return new ClueDto(clue.getLabel(), clue.getDirection(), clue.getText());
    }
}