package ru.aureys.commands;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.aureys.core.CommandMessage;
import ru.aureys.core.annotation.Command;
import ru.aureys.core.annotation.Event;

@Data
@Command
@RequiredArgsConstructor
public class SimpleCommand implements CommandMessage {
    private final String usefulData;
}

