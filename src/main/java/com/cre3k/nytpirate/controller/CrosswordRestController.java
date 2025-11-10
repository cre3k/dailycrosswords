package com.cre3k.nytpirate.controller;

import com.cre3k.nytpirate.services.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CrosswordRestController {

    @Autowired
    CrosswordService crosswordService;

    @PostMapping("/check")
    public boolean checkAnswers(@RequestBody List<String> answersToCheck) {
        return crosswordService.checkCurrentCrossword(answersToCheck);
    }

    @PostMapping("/reveal")
    public String revealLetter(@RequestBody int index) {
        return crosswordService.revealLetter(index);
    }

    @PostMapping("/autocheck")
    public List<Integer> revealLetter(@RequestBody List<String> answersToCheck) {
        return crosswordService.autocheckCurrentCrossword(answersToCheck);
    }
}
