package com.cre3k.nytpirate.services;

import com.cre3k.nytpirate.model.*;
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

    public String getTodayCrosswordJson(CrosswordType type) {
        return restClient.get()
                .uri("/svc/crosswords/v6/puzzle/{type}.json", type.getType())
                .header("x-games-auth-bypass", "true")
                .retrieve()
                .body(String.class);
    }
}
