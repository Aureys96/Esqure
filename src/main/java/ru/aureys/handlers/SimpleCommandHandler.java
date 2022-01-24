package ru.aureys.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.CommandHandler;
import ru.aureys.core.ExecutionContext;
import ru.aureys.events.SimpleEvent;

import static java.lang.String.format;

@Slf4j
@Component
public class SimpleCommandHandler extends CommandHandler {

    public SimpleCommandHandler() {
        add(SimpleCommand.class, this::handle);
    }

    private void handle(SimpleCommand command) {

        log.info("Command received: {}", command);

        final ExecutionContext execution = begin(command);
        try {
            SimpleEvent event = new SimpleEvent(format("Received useful data from command %s: %s",
                    command.getClass().getSimpleName(), command.getUsefulData()));
            execution.inContext(event);
        } finally {
            execution.commit();
        }
    }
}
