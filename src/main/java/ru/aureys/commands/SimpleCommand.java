package ru.aureys.commands;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.aureys.core.Command;

@Data
@RequiredArgsConstructor
public class SimpleCommand implements Command {
    private final String usefulData;
}
