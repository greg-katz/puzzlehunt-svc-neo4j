package com.horshers.puzzlehunt.springdata.repo;

import com.horshers.puzzlehunt.springdata.model.Hunt;
import com.horshers.puzzlehunt.springdata.model.Team;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface TeamRepository extends Neo4jRepository<Team, UUID> {

  Team findAllByHunt(Hunt hunt);

  Team findByHuntAndName(Hunt hunt, String name);

  Team findByHuntAndName(UUID hunt, String name);

  Team findByName(String name);
}