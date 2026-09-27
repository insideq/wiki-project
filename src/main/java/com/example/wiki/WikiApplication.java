package com.example.wiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WikiApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final var strArgs = String.join(" ", args);
        if (!strArgs.isBlank()) {
            logger.info(strArgs);
        }
    }
}
