package ru.aureys.controllers;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.bus.IBus;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final IBus bus;

    @PostMapping("/test")
    public String test(@RequestBody SimpleDto dto) {
        final SimpleCommand command = new SimpleCommand(dto.getUsefulData());
        bus.publish(command);
        return "success";
    }

    @Getter
    @NoArgsConstructor(force = true)
    private static class SimpleDto {
        private final String usefulData;
    }

}
