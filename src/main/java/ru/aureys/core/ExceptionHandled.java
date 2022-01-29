package ru.aureys.core;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ExceptionHandled {
    private final String exceptionDescription;
    private final Exception cause;
}
