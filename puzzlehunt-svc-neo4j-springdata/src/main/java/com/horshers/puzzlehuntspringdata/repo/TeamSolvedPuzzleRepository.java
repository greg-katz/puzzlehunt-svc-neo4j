package com.horshers.puzzlehuntspringdata.repo;

import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface TeamSolvedPuzzleRepository extends Neo4jRepository<TeamSolvedPuzzle, UUID> {
}