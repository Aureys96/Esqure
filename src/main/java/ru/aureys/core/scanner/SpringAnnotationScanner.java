package ru.aureys.core.scanner;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import ru.aureys.core.annotation.Handle;
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

    public Map<Class<?>, Collection<Consumer<?>>> registerHandlers() {
        final Map<Class<?>, Collection<Consumer<?>>> registrations = new HashMap<>();
        scanForCommandHandlers(Handler.class, registrations);
        scanForCommandHandlers(Saga.class, registrations);
        log.debug("Found {} registered handlers", registrations.size());

        return registrations;
    }

    private void scanForCommandHandlers(Class<? extends Annotation> annotationForLookup,
                                        Map<Class<?>, Collection<Consumer<?>>> registrations) {
        final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(annotationForLookup);
        beansWithAnnotation.values().forEach(handler -> {
            final Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotationUtils.findAnnotation(method, Handle.class) != null) {
                    Consumer<?> consumer = (command) -> getConsumer(handler, method, command);
                    addRegistration(method.getParameterTypes()[0], consumer, registrations);
                }
            }
        } );
    }

    @SneakyThrows
    private Object getConsumer(Object handler, Method method, Object command) {
        return method.invoke(handler, command);
    }

    public void addRegistration(Class<?> clazz, Consumer<?> consumer,
                                Map<Class<?>, Collection<Consumer<?>>> registrations) {
        registrations.putIfAbsent(clazz, new ArrayList<>());
        registrations.get(clazz).add(consumer);
    }

}
