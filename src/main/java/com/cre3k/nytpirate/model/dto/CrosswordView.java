package com.cre3k.nytpirate.model.dto;

import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;

import java.time.LocalDate;
import java.util.List;

public record CrosswordView(
        List<CellDto> cells,
        List<Clue> across,
        List<Clue> down,
        LocalDate date,
        int height,
        int width,
        String startAsset,
        String solveAsset
) {

    public static CrosswordView from(Crossword crossword, LocalDate date) {
        List<CellDto> cells = crossword.getCells().stream()
                .map(CellDto::from).toList();
        List<Clue> across = crossword.getClues().stream()
                .filter(clue -> clue.getDirection() == Direction.ACROSS).toList();
        List<Clue> down = crossword.getClues().stream()
                .filter(clue -> clue.getDirection() == Direction.DOWN).toList();

        return new CrosswordView(cells, across, down, date,
                crossword.getHeight(), crossword.getWidth(),
                crossword.getStartAsset(), crossword.getSolveAsset());
    }
}
