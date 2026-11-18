package com.cre3k.nytpirate.model.dto;

import com.cre3k.nytpirate.model.Crossword;

import java.time.LocalDate;
import java.util.List;

public record CrosswordDto(
        LocalDate date,
        int width,
        int height,
        List<CellDto> cells,
        List<ClueDto> clues,
        String startAsset,
        String solveAsset
) {
    public static CrosswordDto from(Crossword crossword, LocalDate date) {
        return new CrosswordDto(
                date,
                crossword.getWidth(),
                crossword.getHeight(),
                crossword.getCells().stream().map(CellDto::from).toList(),
                crossword.getClues().stream().map(ClueDto::from).toList(),
                crossword.getStartAsset(),
                crossword.getSolveAsset()
        );
    }
}