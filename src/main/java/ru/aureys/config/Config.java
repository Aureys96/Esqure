package ru.aureys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.aureys.core.bus.Bus;
import ru.aureys.core.bus.BusFactory;

@Configuration
public class Config {

    @Bean
    public Bus createBusBean() {
        return BusFactory.concurrentBus();
    }

}
