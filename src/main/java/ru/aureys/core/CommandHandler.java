package ru.aureys.core;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Slf4j
public class CommandHandler extends Handler {


    @SuppressWarnings("WeakerAccess")
    protected ExecutionContext begin(CommandMessage command) {
        return new DefaultExecutionContext(command);
    }

    private class DefaultExecutionContext implements ExecutionContext {

        private static final int INITIAL_CAPACITY = 10;

        private final CommandMessage command;
        private final String id = UUID.randomUUID().toString();
        private final List<EventMessage> fails = new ArrayList<>(INITIAL_CAPACITY);
        private final List<EventMessage> executionUnit = new ArrayList<>(INITIAL_CAPACITY);

        private DefaultExecutionContext(CommandMessage command) {
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
                for (EventMessage fail : fails) {
                    CommandHandler.super.emit(fail);
                }
                log.debug("{} rollback", this);
                fails.clear();
            }
            executionUnit.clear();
        }

        @Override
        public void inContext(EventMessage event) {
            executionUnit.add(requireNonNull(event));
            log.debug("{} append {} to execution context", this, event.getClass().getSimpleName());
        }

        @Override
        public void failWith(EventMessage event) {
            fails.add(requireNonNull(event));
            log.debug("{} append {} to rollback action", this, event.getClass().getSimpleName());
        }

        @Override
        public String toString() {
            return "Context [" + command.getClass().getSimpleName() + "/" + id + "]";
        }

    }
}
