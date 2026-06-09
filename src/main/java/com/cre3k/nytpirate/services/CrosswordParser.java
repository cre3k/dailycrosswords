package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Cell;
import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.google.gson.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        if (root.has("assets")) {
            JsonArray assets = root.getAsJsonArray("assets");

            /**
             * Поисследовал их кроссворды — есть два вида спрайтов. Спрайты, которые видно с начала решения кроссворда,
             * ссылки на них имеют формат
             * "https://www.nytimes.com/games-assets/overlays/{date}.{crosswordType}.start.{ext}".
             * Также есть спрайты, открывающиеся только после решения кроссворда, ссылки на них имеют формат
             * "https://www.nytimes.com/games-assets/overlays/{date}.{crosswordType}.solve.{ext}".
             * Фэйлсейф на случай если у ассета нет признака start или solve — в этом случае он сохранится как стартовый
             * ассет и также будет виден с начала решения кроссворда.
             */
            Map<String, String> assetUrls = StreamSupport.stream(assets.spliterator(), false)
                    .map(e -> e.getAsJsonObject().get("uri").getAsString())
                    .collect(Collectors.toMap(
                            uri -> uri.contains("start") ? "start" : uri.contains("solve") ? "solve" : "start",
                            uri -> uri
                    ));
            crossword.setStartAsset(assetUrls.get("start"));
            crossword.setSolveAsset(assetUrls.get("solve"));
        }


        // Проверим результат
        cellList.forEach(System.out::println);

        return crossword;
    }
}
