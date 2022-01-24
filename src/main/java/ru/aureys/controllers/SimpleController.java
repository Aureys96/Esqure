package ru.aureys.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.Command;
import ru.aureys.core.bus.Bus;
import ru.aureys.misc.Traceable;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final Bus bus;

    @PostMapping("/test")
    @Traceable
    public String test() {
        Command command = new SimpleCommand("1234");
        bus.publish(command);
        return "success";
    }
}
