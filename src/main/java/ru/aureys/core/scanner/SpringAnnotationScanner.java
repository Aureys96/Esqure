package ru.aureys.core.scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
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
import java.util.function.Consumer;

@Slf4j
@Service
public class SpringAnnotationScanner {

    private final ApplicationContext applicationContext;

    public SpringAnnotationScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<Class<?>, Collection<Consumer<Object>>> registerHandlers() {
        final Map<Class<?>, Collection<Consumer<Object>>> registrations = new HashMap<>();
        scanForCommandHandlers(Handler.class, HandleCommand.class, registrations);
        scanForCommandHandlers(Saga.class, HandleEvent.class, registrations);
        log.debug("Found {} registered handlers", registrations.size());

        return registrations;
    }

    private void scanForCommandHandlers(Class<? extends Annotation> typeAnnotationForLookup,
                                        Class<? extends Annotation> classAnnotationForLookup,
                                        Map<Class<?>, Collection<Consumer<Object>>> registrations) {
        final Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(typeAnnotationForLookup);

        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, classAnnotationForLookup) != null) {
                    Consumer<Object> consumer = (command) -> initHandle(handler, method, command);
                    addRegistration(method.getParameterTypes()[0], consumer, registrations);
                }
            }
        } );
    }

    @SneakyThrows
    private void initHandle(Object handler, Method method, Object command) {
        method.invoke(handler, command);
    }

    public void addRegistration(Class<?> clazz, Consumer<Object> consumer,
                                Map<Class<?>, Collection<Consumer<Object>>> registrations) {
        registrations.putIfAbsent(clazz, new ArrayList<>());
        registrations.get(clazz).add(consumer);
    }

}
