package ru.aureys.core;

import lombok.extern.slf4j.Slf4j;
import ru.aureys.core.bus.IBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Slf4j
public class CommandHandler {

    private final IBus bus;

    public CommandHandler(IBus bus) {
        this.bus = bus;
    }

    @SuppressWarnings("WeakerAccess")
    protected ExecutionContext begin(Object command) {
        return new DefaultExecutionContext(command);
    }

    private class DefaultExecutionContext implements ExecutionContext {

        private static final int INITIAL_CAPACITY = 10;

        private final Object command;
        private final String id = UUID.randomUUID().toString();
        private final List<Object> fails = new ArrayList<>(INITIAL_CAPACITY);
        private final List<Object> executionUnit = new ArrayList<>(INITIAL_CAPACITY);

        private DefaultExecutionContext(Object command) {
            this.command = requireNonNull(command);
            log.debug("{} created", this);
        }

        @Override
        public void commit() {
            log.debug("{} committing...", this);
            if (fails.isEmpty()) {
                executionUnit.forEach(bus::publish);
                log.debug("{} {} unit(s) committed", this, executionUnit.size());
            } else {
                for (Object fail : fails) {
                    bus.publish(fail);
                }
                log.debug("{} rollback", this);
                fails.clear();
            }
            executionUnit.clear();
        }

        @Override
        public void inContext(Object event) {
            executionUnit.add(requireNonNull(event));
            log.debug("{} append {} to execution context", this, event.getClass().getSimpleName());
        }

        @Override
        public void failWith(Object event) {
            fails.add(requireNonNull(event));
            log.debug("{} append {} to rollback action", this, event.getClass().getSimpleName());
        }

        @Override
        public String toString() {
            return "Context [" + command.getClass().getSimpleName() + "/" + id + "]";
        }

    }
}
