package ru.aureys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import ru.aureys.core.bus.ConcurrentBus;
import ru.aureys.core.bus.IBus;

@Configuration
public class Config {

    @Bean
    @Primary
    public IBus lazyBusAdapterBean(@Lazy ConcurrentBus bus) {
        return bus;
    }
}
