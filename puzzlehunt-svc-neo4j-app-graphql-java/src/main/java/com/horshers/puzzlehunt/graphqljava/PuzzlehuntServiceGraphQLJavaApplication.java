package com.horshers.puzzlehunt.graphqljava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.horshers.puzzlehuntspringdata")
@ComponentScan("com.horshers.puzzlehuntdriver")
@ComponentScan("com.horshers.puzzlehuntogm")
@ComponentScan("com.horshers.puzzlehuntgraphql")
public class PuzzlehuntServiceGraphQLJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuzzlehuntServiceGraphQLJavaApplication.class, args);
	}
}
