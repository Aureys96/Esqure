package ru.aureys.core.bus;

/**
 * Фабрика для создания необходимого типа шины.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class BusFactory {

    public static IBus concurrentBus() {
        return ConcurrentBus.INSTANCE;
    }

}
