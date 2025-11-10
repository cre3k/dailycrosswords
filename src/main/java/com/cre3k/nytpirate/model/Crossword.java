package com.cre3k.nytpirate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Crossword {

    private List<Cell> cells;
    private List<Clue> clues;
    private int height;
    private int width;

}
