package ru.aureys.core;

public interface ExecutionContext {

    void inContext(Object event);

    void failWith(Object event);

    void commit();

}

