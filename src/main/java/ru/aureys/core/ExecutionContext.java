package ru.aureys.core;

public interface ExecutionContext {

    void inContext(Event event);

    void failWith(Event event);

    void commit();

}

