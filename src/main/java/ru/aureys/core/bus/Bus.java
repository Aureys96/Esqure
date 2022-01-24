package ru.aureys.core.bus;

import ru.aureys.core.Registration;

public interface Bus {

    <T extends BusMessage> void addRegistration(final Registration<T> registration);

    void publish(BusMessage message);

}
