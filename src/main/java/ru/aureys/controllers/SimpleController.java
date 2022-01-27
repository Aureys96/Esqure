package ru.aureys.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.CommandMessage;
import ru.aureys.core.bus.IBus;
import ru.aureys.misc.Traceable;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final IBus bus;

    @PostMapping("/test")
    public String test() {
        CommandMessage command = new SimpleCommand("1234");
        bus.publish(command);
        return "success";
    }
}
