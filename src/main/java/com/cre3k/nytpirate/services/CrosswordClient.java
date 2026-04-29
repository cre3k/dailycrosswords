package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.Cell;
import com.cre3k.nytpirate.model.Clue;
import com.cre3k.nytpirate.model.Crossword;
import com.cre3k.nytpirate.model.Direction;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrosswordClient {
    @Autowired
    RestClient restClient;

    public String getTodayCrosswordJson() {
        return restClient.get()
                .uri("/svc/crosswords/v6/puzzle/mini.json")
                .header("x-games-auth-bypass", "true")
                .retrieve()
                .body(String.class);
    }
}
