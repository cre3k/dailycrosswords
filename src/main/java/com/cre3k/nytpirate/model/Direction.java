package com.cre3k.nytpirate.model;



public enum Direction {
    ACROSS ("Across"),
    DOWN ("Down");

    private final String title;

    Direction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Direction fromTitle(String title) {
        for (Direction dir : values()) {
            if (dir.getTitle().equalsIgnoreCase(title)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("No enum constant with title " + title);
    }
}
