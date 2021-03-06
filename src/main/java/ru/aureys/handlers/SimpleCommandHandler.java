package ru.aureys.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.annotation.HandleCommand;
import ru.aureys.core.annotation.Handler;
import ru.aureys.events.SimpleEvent;

import static java.lang.String.format;

@Slf4j
@Handler
public class SimpleCommandHandler {

    @HandleCommand
    public void handle(SimpleCommand command, ExecutionContext execution) {
        if (command.getUsefulData().equals("InvalidData"))
            throw new IllegalArgumentException("Oopsie happened!");

        final SimpleEvent event = new SimpleEvent(format("Received useful data from command %s: %s",
                command.getClass().getSimpleName(), command.getUsefulData()));
        execution.inContext(event);
    }
}
