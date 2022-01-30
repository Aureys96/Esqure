package ru.aureys.core.scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.annotation.HandleCommand;
import ru.aureys.core.annotation.HandleEvent;
import ru.aureys.core.annotation.Handler;
import ru.aureys.core.annotation.Saga;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@Service
public class SpringAnnotationScanner {

    private final ApplicationContext applicationContext;

    public SpringAnnotationScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<Class<?>, Collection<Consumer<Object>>> registerEventHandlers() {
        final Map<Class<?>, Collection<Consumer<Object>>> registrations = new HashMap<>();
        scanForEventHandlers(Saga.class, HandleEvent.class, registrations);
        log.debug("Found {} registered event handlers", registrations.size());

        return registrations;
    }

    public Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> registerCommandHandlers() {
        final Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> registrations = new HashMap<>();
        scanForCommandHandlers(Handler.class, HandleCommand.class, registrations);
        log.debug("Found {} registered command handlers", registrations.size());

        return registrations;
    }

    private void scanForEventHandlers(Class<? extends Annotation> typeAnnotationForLookup,
                                      Class<? extends Annotation> classAnnotationForLookup,
                                      Map<Class<?>, Collection<Consumer<Object>>> eventRegistrations) {
        final Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(typeAnnotationForLookup);

        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, classAnnotationForLookup) != null) {

                    final Consumer<Object> consumer = (command) -> initHandle(handler, method, command);
                    addEventRegistration(method.getParameterTypes()[0], consumer, eventRegistrations);

                }
            }
        });
    }

    private void scanForCommandHandlers(Class<? extends Annotation> typeAnnotationForLookup,
                                        Class<? extends Annotation> classAnnotationForLookup,
                                        Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> commandRegistrations) {
        final Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(typeAnnotationForLookup);

        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, classAnnotationForLookup) != null) {
                    final BiConsumer<Object, ExecutionContext> consumer =
                            (command, context) -> initHandleCommand(handler, method, command, context);
                    addCommandRegistration(method.getParameterTypes()[0], consumer, commandRegistrations);
                }
            }
        });
    }

    @SneakyThrows
    private void initHandleCommand(Object handler, Method method, Object command, ExecutionContext execution) {
        method.invoke(handler, command, execution);
    }

    @SneakyThrows
    private void initHandle(Object handler, Method method, Object command) {
        method.invoke(handler, command);
    }

    public void addCommandRegistration(Class<?> clazz, BiConsumer<Object, ExecutionContext> consumer,
                                       Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> registrations) {
        registrations.putIfAbsent(clazz, new ArrayList<>());
        registrations.get(clazz).add(consumer);
    }

    public void addEventRegistration(Class<?> clazz, Consumer<Object> consumer,
                                     Map<Class<?>, Collection<Consumer<Object>>> registrations) {
        registrations.putIfAbsent(clazz, new ArrayList<>());
        registrations.get(clazz).add(consumer);
    }

}
