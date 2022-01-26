package ru.aureys.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.CommandHandler;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.annotation.Handle;
import ru.aureys.core.annotation.Handler;
import ru.aureys.events.SimpleEvent;

import static java.lang.String.format;

@Slf4j
@Handler
@Component
public class SimpleCommandHandler extends CommandHandler {

    public SimpleCommandHandler() {
        add(SimpleCommand.class, this::handle);
    }


    @Handle
    private int doStuff(int a, float b) {
        return 1;
    }

    @Handle
    public void handle(SimpleCommand command) {

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
