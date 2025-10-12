package com.pokeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        // Capa de Aplicación
        "com.pokeapi.application.soap",     // Endpoints SOAP
        // Capa de Dominio
        "com.pokeapi.domain.service",       // Servicios de dominio
        "com.pokeapi.domain.model",         // Entidades de dominio
        "com.pokeapi.domain.port",          // Puertos/Interfaces
        // Capa de Infraestructura
        "com.pokeapi.infrastructure.adapter",    // Adaptadores (REST, Repository)
        "com.pokeapi.infrastructure.config",     // Configuraciones
        "com.pokeapi.infrastructure.persistence", // Entidades y repos JPA
        "com.pokeapi.infrastructure.aspect"      // Aspectos (logging, etc)
    }
)
public class PokeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokeApiApplication.class, args);
	}

}
