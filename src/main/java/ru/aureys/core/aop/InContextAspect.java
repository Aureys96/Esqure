package ru.aureys.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.aureys.core.CommandHandler;
import ru.aureys.core.ExceptionHandled;
import ru.aureys.core.ExecutionContext;
import ru.aureys.core.bus.IBus;

@Slf4j
@Aspect
@Component
public class InContextAspect extends CommandHandler {

    public InContextAspect(@Lazy IBus bus) {
        super(bus);
    }

    @Around("@annotation(ru.aureys.core.annotation.HandleCommand)")
    public void transactionalizeHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object command = joinPoint.getArgs()[0];
        log.info("Command received: {}", command);
        final ExecutionContext context = begin(command);
        try {
             joinPoint.proceed(new Object[]{command, context});
        } catch (Exception e) {
            log.error("{} execution failed with Exception: {}, message: {}", command, e.getClass(), e.getMessage());
            context.failWith(new ExceptionHandled(e.getMessage(), e));
        } finally {
            context.commit();
        }
    }

}
