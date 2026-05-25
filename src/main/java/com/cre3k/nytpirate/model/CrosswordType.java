package com.cre3k.nytpirate.model;

public enum CrosswordType {

    MINI("mini"),
    MIDI("midi"),
    MAXY("daily");

    private String type;
    CrosswordType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
