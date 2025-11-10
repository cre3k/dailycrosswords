package com.cre3k.nytpirate.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cell {
    private String answer;
    @SerializedName("clues")
    private List<Integer> clueIds;
    private String label;
}
