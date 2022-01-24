package ru.aureys.core.bus;

/**
 * Фабрика для создания необходимого типа шины.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class BusFactory {

    public static Bus concurrentBus() {
        return ConcurrentBus.INSTANCE;
    }

}
