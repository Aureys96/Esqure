package ru.aureys.sagas;

import lombok.extern.slf4j.Slf4j;
import ru.aureys.core.ExceptionHandled;
import ru.aureys.core.annotation.HandleEvent;
import ru.aureys.core.annotation.Saga;

@Slf4j
@Saga
public class SimpleErrorSaga {

    @HandleEvent
    public void handleException(ExceptionHandled event) {
        log.error("ExceptionHandled event received: {}", event);
    }
}
