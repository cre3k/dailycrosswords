package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Cell;
import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.cre3k.nytpirate.persistence.CrosswordEntity;
import com.cre3k.nytpirate.persistence.CrosswordRepository;
import com.cre3k.nytpirate.session.UserSession;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CrosswordServiceImpl implements CrosswordService {

    @Autowired
    UserSession userSession;

    @Autowired
    CrosswordRepository crosswordRepository;

    @Autowired
    CrosswordParser parser;

    @Autowired
    CrosswordClient client;

    private Crossword getTodayCrosswordWithAnswers() {

        LocalDate currentDate = LocalDate.now(ZoneId.of(NYT_TIMEZONE));

        return getCrosswordByDateWithAnswer(currentDate).orElseGet(() -> {
            saveTodaysCrossword();
            return parser.parseCrosswordFromJson(crosswordRepository.findByDate(currentDate).get().getPayload());
        });
    }


    private Optional<Crossword> getCrosswordByDateWithAnswer(LocalDate date) {
        return crosswordRepository.findByDate(date).map(entity -> parser.parseCrosswordFromJson(entity.getPayload()));
    }

    @Override
    public boolean checkCurrentUserCrossword(List<String> answersToCheck) {
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
    public List<Integer> autocheckCurrentUserCrossword(List<String> answersToCheck) {
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
    public void saveTodaysCrossword() {
        LocalDate currentDate = LocalDate.now(ZoneId.of(NYT_TIMEZONE));
        if (crosswordRepository.existsByDate(currentDate)) {
            return;
        }
        String json = client.getTodayCrosswordJson();

        CrosswordEntity entity = new CrosswordEntity();
        entity.setDate(currentDate);
        entity.setPayload(json);

        crosswordRepository.save(entity);
    }

    @Override
    public Crossword getClientUserCrossword() {
        Crossword crossword = userSession.getCurrentUsersCrossword();
        Crossword copyForClient = new Crossword();
        copyForClient.setHeight(crossword.getHeight());
        copyForClient.setWidth(crossword.getWidth());
        copyForClient.setClues(crossword.getClues());

        List<Cell> cellsCopy = crossword.getCells().stream().map(cell -> new Cell(null, cell.getClueIds(), cell.getLabel())).toList();

        copyForClient.setCells(cellsCopy);

        return copyForClient;
    }

    @Override
    public void configureUserCrossword(LocalDate date) {
        Crossword crossword = getCrosswordByDateWithAnswer(date).get();
        userSession.setCurrentUsersCrossword(crossword);
    }

    @Override
    public void configureTodayUserCrossword() {
        Crossword crossword = getTodayCrosswordWithAnswers();
        userSession.setCurrentUsersCrossword(crossword);
    }


    @Override
    public String revealLetter(int index) {
        Crossword crossword = userSession.getCurrentUsersCrossword();
        return crossword.getCells().get(index).getAnswer();
    }


}
