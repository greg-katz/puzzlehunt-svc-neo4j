package com.horshers.puzzlehuntspringdata.repo;

import com.horshers.puzzlehuntspringdata.model.Puzzle;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface PuzzleRepository extends Neo4jRepository<Puzzle, UUID> {

  Puzzle findByName(String name);
}