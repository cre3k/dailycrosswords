package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.exception.CrosswordNotFoundException;
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
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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
            return parser.parseCrosswordFromJson(crosswordRepository.findByDate(currentDate).orElseThrow(() -> new CrosswordNotFoundException(currentDate)).getPayload());
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
        copyForClient.setStartAsset(crossword.getStartAsset());
        copyForClient.setSolveAsset(crossword.getSolveAsset());

        List<Cell> cellsCopy = crossword.getCells().stream().map(cell -> new Cell(null, cell.getClueIds(), cell.getLabel(), cell.getType())).toList();

        copyForClient.setCells(cellsCopy);

        return copyForClient;
    }

    @Override
    public void configureUserCrossword(LocalDate date) {
        Crossword crossword = getCrosswordByDateWithAnswer(date).orElseThrow(() -> new CrosswordNotFoundException(date));
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

    @Override
    public List<Integer> getArchiveYears() {
        return crosswordRepository.findDistinctYears();
    }

    @Override
    public List<Month> getArchiveMonths(int year) {
        return crosswordRepository.findDistinctMonthsByYear(year)
                .stream().map(Month::of).toList();
    }

    @Override
    public List<LocalDate> getArchiveDates(int year, Month month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return crosswordRepository.findDatesBetween(start, end);
    }

}
