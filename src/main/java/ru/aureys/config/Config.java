package ru.aureys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.aureys.core.bus.IBus;
import ru.aureys.core.bus.BusFactory;

@Configuration
public class Config {

    @Bean
    public IBus createBusBean() {
        return BusFactory.concurrentBus();
    }

}
