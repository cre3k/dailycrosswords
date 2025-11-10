package com.cre3k.nytpirate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NytpirateApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NytpirateApplication.class, args);
	}

}
