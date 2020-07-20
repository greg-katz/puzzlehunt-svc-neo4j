package com.horshers.puzzlehunt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.horshers.puzzlehuntspringdata")
@ComponentScan("com.horshers.puzzlehuntdriver")
@ComponentScan("com.horshers.puzzlehuntogm")
public class PuzzlehuntServiceNeo4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuzzlehuntServiceNeo4jApplication.class, args);
	}
}
