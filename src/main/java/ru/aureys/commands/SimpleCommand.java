package ru.aureys.commands;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.aureys.core.annotation.Command;

@Data
@Command
@RequiredArgsConstructor
public class SimpleCommand {
    private final String usefulData;
}

