package ru.aureys.core;

import ru.aureys.core.bus.BusMessage;

@SuppressWarnings("unused")
public class EventHandler extends Handler {

    @Override
    protected final void emit(BusMessage message) {
        super.emit(message);
    }
}
