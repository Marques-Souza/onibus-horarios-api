package com.marques.onibus_horarios_api;

import org.springframework.boot.SpringApplication;

public class TestOnibusHorariosApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
