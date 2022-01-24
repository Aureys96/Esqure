package ru.aureys.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.aureys.core.Event;

@Data
@AllArgsConstructor
public class SimpleEvent implements Event {
    private String eventData;
}
