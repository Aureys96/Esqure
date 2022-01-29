package ru.aureys.core.bus;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.scanner.SpringAnnotationScanner;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Service
public class ConcurrentBus implements IBus, AutoCloseable {

    private static final int DEFAULT_POOL_SIZE = 30;

    public ConcurrentBus(SpringAnnotationScanner scanner) {
        this.eventRegistrations = scanner.registerEventHandlers();
        this.commandRegistrations = scanner.registerCommandHandlers();
    }

    private final Map<Class<?>, Collection<Consumer<Object>>> eventRegistrations;
    private final Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> commandRegistrations;

    private final ThreadFactory factory = createFactory();
    private final ExecutorService executor = newFixedThreadPool(DEFAULT_POOL_SIZE, factory);

    private static ThreadFactory createFactory() {
        final CustomizableThreadFactory newFactory = new CustomizableThreadFactory("bus_");
        newFactory.setDaemon(true);
        return newFactory;
    }

    @Override
    public void publish(Object message) {
        executor.execute(() -> {
            final Collection<Consumer<Object>> eventConsumers = eventRegistrations.get(message.getClass());
            if (CollectionUtils.isEmpty(eventConsumers)) {
                final Collection<BiConsumer<Object, ExecutionContext>> commandConsumers =
                        commandRegistrations.get(message.getClass());
                commandConsumers.forEach(consumer -> consumer.accept(message, null));

            } else {
                eventConsumers.forEach(consumer -> consumer.accept(message));
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
