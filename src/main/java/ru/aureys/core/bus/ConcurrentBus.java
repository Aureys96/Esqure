package ru.aureys.core.bus;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.CollectionUtils;
import ru.aureys.core.Registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;

public enum ConcurrentBus implements Bus, AutoCloseable {

    INSTANCE;

    private static final int DEFAULT_POOL_SIZE = 30;

    private final Map<Class<? extends BusMessage>, Collection<Consumer<BusMessage>>> registrations = new HashMap<>();

    private final ThreadFactory factory = createFactory();
    private final ExecutorService executor = newFixedThreadPool(DEFAULT_POOL_SIZE, factory);

    private static ThreadFactory createFactory() {
        final CustomizableThreadFactory newFactory = new CustomizableThreadFactory("bus");
        newFactory.setDaemon(true);
        return newFactory;
    }

    @Override
    public <T extends BusMessage> void addRegistration(Registration<T> registration) {
        registrations.putIfAbsent(registration.type(), new ArrayList<>());
        @SuppressWarnings("unchecked")
        final Consumer<BusMessage> consumer = (Consumer<BusMessage>) registration.consumer();
        registrations.get(registration.type()).add(consumer);
    }

    @Override
    public void publish(BusMessage message) {
        executor.execute(() -> {
            final Collection<Consumer<BusMessage>> consumers = registrations.get(message.getClass());
            if (!CollectionUtils.isEmpty(consumers)) {
                consumers.forEach(consumer -> consumer.accept(message));
            }
        });
    }

    @Override
    public void close() {
        if (executor.isShutdown() || executor.isTerminated()) {
            return;
        }
        executor.shutdownNow();
    }

}
