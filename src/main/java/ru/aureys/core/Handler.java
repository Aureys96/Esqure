package ru.aureys.core;

import org.springframework.stereotype.Component;
import ru.aureys.core.bus.Bus;
import ru.aureys.core.bus.BusFactory;
import ru.aureys.core.bus.BusMessage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public abstract class Handler {

    private final Bus bus = BusFactory.concurrentBus();

    private final AtomicBoolean hasHandlers = new AtomicBoolean();

    protected final <T extends BusMessage> void add(Class<T> type, Consumer<T> consumer) {
        add(new Registration<>(type, consumer::accept));
    }

    private <T extends BusMessage> void add(Registration<? super T> registration) {
        bus.addRegistration(registration);
        hasHandlers.set(registration != null);
    }

    void emit(BusMessage message) {
        if (!hasHandlers.get()) {
            throw new IllegalStateException("No message registration was provided for " + getClass());
        }
        bus.publish(message);
    }

}
