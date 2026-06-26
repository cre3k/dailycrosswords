package com.cre3k.nytpirate.model.dto;

import com.cre3k.nytpirate.model.Cell;

import java.util.List;

public record CellDto(List<Integer> clueIds, String label, int type) {
    public boolean isCircled() {
        return type == 2;
    }

    public static CellDto from(Cell cell) {
        return  new CellDto(cell.getClueIds(), cell.getLabel(), cell.getType());
    }
}
