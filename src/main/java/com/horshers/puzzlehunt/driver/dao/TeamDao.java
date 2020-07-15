package com.horshers.puzzlehunt.driver.dao;

import com.horshers.puzzlehunt.driver.model.Player;
import com.horshers.puzzlehunt.driver.model.Team;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TeamDao {

  @Autowired
  private Driver neo;

  public List<Team> getTeams(UUID hunt) {
    String query = """
      match
        (team:Team)-[:PLAYED]->(hunt:Hunt{uuid:$hunt}),
        (team)<-[:CAPTAIN_OF]-(captain:Person),
        (team)<-[:MEMBER_OF]-(player:Person)
      return team, captain, collect(player) as players
      order by team.name
    """;

    try (Session session = neo.session()) {
      return session.run(query, Map.of("hunt", hunt.toString())).list(this::team);
    }
  }

  private Team team(Record record) {
    Team team = new Team();
    team.setId(uuid(record.get("team").get("uuid")));
    team.setName(record.get("team").get("name").asString());
    team.setCaptain(player(record.get("captain")));
    team.setPlayers(record.get("players").asList(this::player));
    return team;
  }

  private Player player(Value value) {
    Player player = new Player();
    player.setId(uuid(value.get("uuid")));
    player.setName(value.get("name").asString());
    return player;
  }

  private UUID uuid(Value value) {
    return UUID.fromString(value.asString());
  }
}