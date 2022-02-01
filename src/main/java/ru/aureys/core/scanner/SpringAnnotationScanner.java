package ru.aureys.core.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.annotation.HandleCommand;
import ru.aureys.core.annotation.HandleEvent;
import ru.aureys.core.annotation.Handler;
import ru.aureys.core.annotation.Saga;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
        scanForEventHandlers(registrations);
        log.debug("Found {} registered event handlers", registrations.size());

        return registrations;
    }

    public Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> registerCommandHandlers() {
        final Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> registrations = new HashMap<>();
        scanForCommandHandlers(registrations);
        log.debug("Found {} registered command handlers", registrations.size());

        return registrations;
    }

    private void scanForEventHandlers(Map<Class<?>, Collection<Consumer<Object>>> eventRegistrations) {
        final Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(Saga.class);

        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, (Class<? extends Annotation>) HandleEvent.class) != null) {
                    registerHandlerMethod(eventRegistrations, handler, method);

                }
            }
        });
    }

    private void registerHandlerMethod(Map<Class<?>, Collection<Consumer<Object>>> eventRegistrations, Object handler, Method method) {
        final Consumer<Object> consumer = command -> initHandleEvent(handler, method, command);
        Class<?> clazz = method.getParameterTypes()[0];
        eventRegistrations.putIfAbsent(clazz, new ArrayList<>());
        eventRegistrations.get(clazz).add(consumer);
    }

    private void scanForCommandHandlers(Map<Class<?>, Collection<BiConsumer<Object, ExecutionContext>>> commandRegistrations) {
        final Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(Handler.class);
        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, (Class<? extends Annotation>) HandleCommand.class) != null) {
                    final BiConsumer<Object, ExecutionContext> consumer =
                            (command, context) -> initHandleCommand(handler, method, command, context);
                    Class<?> clazz = method.getParameterTypes()[0];
                    commandRegistrations.putIfAbsent(clazz, new ArrayList<>());
                    commandRegistrations.get(clazz).add(consumer);
                }
            }
        });
    }

    private void initHandleCommand(Object handler, Method method, Object command, ExecutionContext execution) {
        try {
            method.invoke(handler, command, execution);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception during command handler method registration", e);
            log.error("Method that could not be registered {} for a command {}", method.getName(), command.getClass());
            throw new RuntimeException(e);
        }
    }

    private void initHandleEvent(Object handler, Method method, Object command) {
        try {
            method.invoke(handler, command);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception during event handler method registration", e);
            log.error("Method that could not be registered {} for an event {}", method.getName(), command.getClass());
            throw new RuntimeException(e);
        }
    }

}
