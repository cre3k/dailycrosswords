package com.cre3k.nytpirate.session;

import com.cre3k.nytpirate.model.Crossword;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Data
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private Crossword currentUsersCrossword;

    @PostConstruct
    private void hi(){
        System.out.println("user inits");
    }
}
