package ru.aureys.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.aureys.core.annotation.Event;

@Data
@Event
@AllArgsConstructor
public class SimpleEvent {
    private String eventData;
}
