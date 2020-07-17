package com.horshers.puzzlehuntspringdata.repo;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Team;
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