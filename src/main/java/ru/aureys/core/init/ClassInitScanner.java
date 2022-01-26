package ru.aureys.core.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ClassInitScanner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("RUN!");
    }
}
