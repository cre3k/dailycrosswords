package com.cre3k.nytpirate.controller;


import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.cre3k.nytpirate.persistence.CrosswordRepository;
import com.cre3k.nytpirate.services.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CrosswordService crosswordService;
    @Autowired
    CrosswordRepository crosswordRepository;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/crossword/current")
    public String currentCrossword(Model model) {
        crosswordService.configureTodayUserCrossword();
        Crossword crossword = crosswordService.getClientUserCrossword();
        List<Clue> acrossClues = crossword.getClues().stream()
                .filter(clue -> clue.getDirection().equals(Direction.ACROSS)).toList();
        List<Clue> downClues = crossword.getClues().stream()
                .filter(clue -> clue.getDirection().equals(Direction.DOWN)).toList();
        model.addAttribute("crossword", crossword);
        model.addAttribute("acrossClues", acrossClues);
        model.addAttribute("downClues", downClues);
        return "crossword";
    }

    @GetMapping("/archive")
    public String showButtons(Model model) {
        List<LocalDate> buttons = crosswordRepository.findAllDates();
        model.addAttribute("crosswordEntities", buttons);
        return "archive";
    }

    @GetMapping("/archive/{date}")
    public String crosswordByDatePage(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              Model model) {
        model.addAttribute("date", date);
        return "archiveCrossword";
    }

    @GetMapping("/crossword/{date}")
    public String crosswordByDate(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  Model model) {
        crosswordService.configureUserCrossword(date);
        Crossword crossword = crosswordService.getClientUserCrossword();
        List<Clue> acrossClues = crossword.getClues().stream()
                .filter(clue -> clue.getDirection().equals(Direction.ACROSS)).toList();
        List<Clue> downClues = crossword.getClues().stream()
                .filter(clue -> clue.getDirection().equals(Direction.DOWN)).toList();
        model.addAttribute("crossword", crossword);
        model.addAttribute("acrossClues", acrossClues);
        model.addAttribute("downClues", downClues);
        return "crossword";
    }

}