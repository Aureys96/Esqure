package ru.aureys.core;

import lombok.extern.slf4j.Slf4j;
import ru.aureys.core.bus.BusMessage;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@Slf4j
@SuppressWarnings("WeakerAccess")
public class Registration<T extends BusMessage> {

    private static final String ERROR = "\nUncaught exception in Registration\n";

    private final Class<T> type;
    private final Consumer<T> consumer;

    public Registration(Class<T> type, Consumer<T> consumer) {
        requireNonNull(consumer, "given consumer must not be null");
        this.type = requireNonNull(type, "given class must not be null");
        this.consumer = (T element) -> {
            try {
                consumer.accept(element);
            } catch (Exception e) {
                log.error(ERROR);
                log.error("Exception details:", e);
            }
        };
    }

    public Consumer<T> consumer() {
        return consumer;
    }

    public Class<T> type() {
        return type;
    }
}
