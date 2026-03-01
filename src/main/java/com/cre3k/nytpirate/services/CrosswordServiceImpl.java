package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Cell;
import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.cre3k.nytpirate.session.UserSession;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CrosswordServiceImpl implements CrosswordService {
    private static final String REQUEST_URL = "https://www.nytimes.com/svc/crosswords/v6/puzzle/mini.json";
    private static final String NYT_TIMEZONE = "America/New_York";

    RestClient restClient = RestClient.create();

    @Autowired
    UserSession userSession;

    @Override
    public Crossword getCurrentCrossword() {
        Crossword crossword = userSession.getCurrentUsersCrossword();

        if (crossword == null) {
            crossword = getCurrentCrosswordWithAnswers();
            userSession.setCurrentUsersCrossword(crossword);
        }

        // Создаем копию кроссворда для клиента без ответов
        Crossword copyForClient = new Crossword();
        copyForClient.setHeight(crossword.getHeight());
        copyForClient.setWidth(crossword.getWidth());
        copyForClient.setClues(crossword.getClues());

        List<Cell> cellsCopy = crossword.getCells().stream()
                .map(cell -> new Cell(
                        null,
                        cell.getClueIds(),
                        cell.getLabel()
                ))
                .toList();

        copyForClient.setCells(cellsCopy);

        return copyForClient;
    }


    private Crossword getCurrentCrosswordWithAnswers() {
        LocalDate currentDate = LocalDate.now(ZoneId.of(NYT_TIMEZONE));

        String response = restClient.get()
                .uri(REQUEST_URL)
                .header("x-games-auth-bypass", "true") // без этого хедера будет 403 forbidden
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        Gson gson = new Gson();
        JsonObject root = JsonParser.parseString(response).getAsJsonObject();
        Crossword crossword = new Crossword();

        JsonArray body = root.getAsJsonArray("body");

        JsonObject board = body.get(0).getAsJsonObject();
        JsonArray cells = board.getAsJsonArray("cells");

        List<Cell> cellList = new ArrayList<>();
        for (JsonElement element : cells) {
            cellList.add(gson.fromJson(element, Cell.class));
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
        System.out.println(response);
        crossword.setCells(cellList);

        return crossword;
    }

    @Override
    public boolean checkCurrentCrossword(List<String> answersToCheck) {
        Crossword crossword = userSession.getCurrentUsersCrossword();
        if (crossword.getCells().size() != answersToCheck.size()) {
            return false;
        }
        for (int i = 0; i < crossword.getCells().size(); ++i) {
            if (answersToCheck.get(i).isEmpty()) {
                continue;
            }
            if (!Objects.equals(crossword.getCells().get(i).getAnswer(), answersToCheck.get(i).toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Integer> autocheckCurrentCrossword(List<String> answersToCheck) {
        Crossword crossword = userSession.getCurrentUsersCrossword();
        List<Integer> correctLetters = new ArrayList<>();
        if (crossword.getCells().size() != answersToCheck.size()) {
            return correctLetters;
        }
        for (int i = 0; i < crossword.getCells().size(); ++i) {
            if (answersToCheck.get(i).isEmpty()) {
                continue;
            }
            if (Objects.equals(crossword.getCells().get(i).getAnswer(), answersToCheck.get(i).toUpperCase())) {
                correctLetters.add(i);
            }
        }
        return correctLetters;
    }

    @Override
    public String revealLetter(int index) {
        Crossword crossword = userSession.getCurrentUsersCrossword();
        return crossword.getCells().get(index).getAnswer();
    }
}
