package ru.aureys.core.bus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.aureys.core.ExceptionHandled;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.scanner.SpringAnnotationScanner;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@Service
public class ConcurrentBus implements IBus, AutoCloseable {

    private static final int DEFAULT_POOL_SIZE = 30;

    public ConcurrentBus(SpringAnnotationScanner scanner) {
        log.debug("Initializing bus");
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
            final Collection<BiConsumer<Object, ExecutionContext>> commandConsumers =
                    commandRegistrations.get(message.getClass());
            if (!CollectionUtils.isEmpty(commandConsumers)) {
                commandConsumers.forEach(consumer -> consumer.accept(message, null));

            } else if (!CollectionUtils.isEmpty(eventConsumers)){
                eventConsumers.forEach(consumer -> consumer.accept(message));
            } else if (message instanceof ExceptionHandled){
                log.error("ExceptionHandled event received: {}", message);
                log.warn("You should consider implementing dedicated ErrorSaga handler for this event!");
            } else {
                log.error("No handler registered for event of type: {}", message.getClass());
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
