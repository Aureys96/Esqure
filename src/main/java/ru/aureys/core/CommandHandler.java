package ru.aureys.core;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Slf4j
public class CommandHandler extends Handler {


    @SuppressWarnings("WeakerAccess")
    protected ExecutionContext begin(Command command) {
        return new DefaultExecutionContext(command);
    }

    private class DefaultExecutionContext implements ExecutionContext {

        private static final int INITIAL_CAPACITY = 10;

        private final Command command;
        private final String id = UUID.randomUUID().toString();
        private final List<Event> fails = new ArrayList<>(INITIAL_CAPACITY);
        private final List<Event> executionUnit = new ArrayList<>(INITIAL_CAPACITY);

        private DefaultExecutionContext(Command command) {
            this.command = requireNonNull(command);
            log.debug("{} created", this);
        }

        @Override
        public void commit() {
            log.debug("{} committing...", this);
            if (fails.isEmpty()) {
                executionUnit.forEach(CommandHandler.super::emit);
                log.debug("{} {} unit(s) committed", this, executionUnit.size());
            } else {
                for (Event fail : fails) {
                    CommandHandler.super.emit(fail);
                }
                log.debug("{} rollback", this);
                fails.clear();
            }
            executionUnit.clear();
        }

        @Override
        public void inContext(Event event) {
            executionUnit.add(requireNonNull(event));
            log.debug("{} append {} to execution context", this, event.getClass().getSimpleName());
        }

        @Override
        public void failWith(Event event) {
            fails.add(requireNonNull(event));
            log.debug("{} append {} to rollback action", this, event.getClass().getSimpleName());
        }

        @Override
        public String toString() {
            return "Context [" + command.getClass().getSimpleName() + "/" + id + "]";
        }

    }
}
