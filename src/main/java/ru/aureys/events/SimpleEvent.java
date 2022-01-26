package ru.aureys.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.aureys.core.EventMessage;
import ru.aureys.core.annotation.Event;

@Data
@Event
@AllArgsConstructor
public class SimpleEvent implements EventMessage {
    private String eventData;
}
