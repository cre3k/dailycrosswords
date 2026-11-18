package com.cre3k.nytpirate.controller;

import com.cre3k.nytpirate.model.dto.CrosswordDto;
import com.cre3k.nytpirate.services.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CrosswordRestController {

    @Autowired
    CrosswordService crosswordService;

    @PostMapping("/check")
    public boolean checkAnswers(@RequestBody List<String> answersToCheck) {
        return crosswordService.checkCurrentUserCrossword(answersToCheck);
    }

    @PostMapping("/reveal")
    public String revealLetter(@RequestBody int index) {
        return crosswordService.revealLetter(index);
    }

    @PostMapping("/autocheck")
    public List<Integer> revealLetter(@RequestBody List<String> answersToCheck) {
        return crosswordService.autocheckCurrentUserCrossword(answersToCheck);
    }

    @GetMapping(value = "/api/crosswords/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public CrosswordDto currentCrossword() {
        LocalDate date = crosswordService.configureTodayUserCrossword();
        return crosswordService.getClientDto(date);
    }
}
