package ru.aureys.core;

public interface ExecutionContext {

    void inContext(EventMessage event);

    void failWith(EventMessage event);

    void commit();

}

