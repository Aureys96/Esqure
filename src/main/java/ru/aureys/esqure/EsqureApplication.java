package ru.aureys.esqure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.aureys")
public class EsqureApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsqureApplication.class, args);
	}

}
