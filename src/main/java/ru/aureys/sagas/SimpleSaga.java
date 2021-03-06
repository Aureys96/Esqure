package ru.aureys.sagas;

import lombok.extern.slf4j.Slf4j;
import ru.aureys.core.annotation.HandleEvent;
import ru.aureys.core.annotation.Saga;
import ru.aureys.core.bus.IBus;
import ru.aureys.events.SimpleEvent;

@Slf4j
@Saga
public class SimpleSaga {

    private final IBus bus;

    public SimpleSaga(IBus bus) {
        this.bus = bus;
    }

    @HandleEvent
    public void handle(SimpleEvent event) {
        log.info("Event received: {}", event);

        // emit(new SimpleCommand("Even more useful data"));
    }
}
