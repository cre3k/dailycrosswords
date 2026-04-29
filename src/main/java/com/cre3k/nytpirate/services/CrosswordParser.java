package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Cell;
import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.google.gson.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrosswordParser {
    private static final Gson GSON = new Gson();

    public Crossword parseCrosswordFromJson(String crosswordJson) {
        JsonObject root = JsonParser.parseString(crosswordJson).getAsJsonObject();
        Crossword crossword = new Crossword();

        JsonArray body = root.getAsJsonArray("body");

        JsonObject board = body.get(0).getAsJsonObject();
        JsonArray cells = board.getAsJsonArray("cells");

        List<Cell> cellList = new ArrayList<>();
        for (JsonElement element : cells) {
            cellList.add(GSON.fromJson(element, Cell.class));
        }
        crossword.setCells(cellList);

        List<Clue> clueList = new ArrayList<>();
        JsonArray clues = board.getAsJsonArray("clues");
        for (int i = 0; i < clues.size(); ++i) {
            JsonObject clue = clues.get(i).getAsJsonObject();
            clueList.add(new Clue(Direction.fromTitle(clue.get("direction").getAsString()), clue.getAsJsonArray("text").get(0).getAsJsonObject().get("plain").getAsString(), clue.get("label").getAsString()));
        }
        crossword.setClues(clueList);
        crossword.setHeight(board.getAsJsonObject("dimensions").getAsJsonPrimitive("height").getAsInt());
        crossword.setWidth(board.getAsJsonObject("dimensions").getAsJsonPrimitive("width").getAsInt());


        // Проверим результат
        cellList.forEach(System.out::println);

        return crossword;
    }
}
