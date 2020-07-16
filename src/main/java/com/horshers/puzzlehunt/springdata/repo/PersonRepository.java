package com.horshers.puzzlehunt.springdata.repo;

import com.horshers.puzzlehunt.springdata.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface PersonRepository extends Neo4jRepository<Person, UUID> {

  Person findByName(String name);

}
