package ru.aureys.sagas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.aureys.commands.SimpleCommand;
import ru.aureys.core.EventHandler;
import ru.aureys.events.SimpleEvent;

@Slf4j
@Component
public class SimpleSaga extends EventHandler {

    public SimpleSaga() {
        add(SimpleEvent.class, this::handle);
    }

    private void handle(SimpleEvent event) {
        log.info("Event received: {}", event);

        //emit(new SimpleCommand("Even more useful data"));
    }
}
