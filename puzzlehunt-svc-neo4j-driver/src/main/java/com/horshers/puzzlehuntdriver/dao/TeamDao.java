package com.horshers.puzzlehuntdriver.dao;

import com.horshers.puzzlehuntdriver.model.Player;
import com.horshers.puzzlehuntdriver.model.Team;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NullValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TeamDao {

  @Autowired
  @Qualifier("neo-driver")
  private Driver neo;

  public List<Team> getTeams(UUID hunt) {
    String query = """
      match
        (team:Team)-[:PLAYED]->(hunt:Hunt{uuid:$hunt})
      optional match
        (team)<-[:CAPTAIN_OF]-(captain:Person),
        (team)<-[:MEMBER_OF]-(player:Person)
      return team, captain, collect(player) as players
      order by team.name
      """;

    try (Session session = neo.session()) {
      return session.run(query, Map.of("hunt", hunt.toString())).list(this::team);
    }
  }

  public Team getTeam(UUID hunt, UUID team) {
    String query = """
      match
        (team:Team{uuid:$team})-[:PLAYED]->(hunt:Hunt{uuid:$hunt})
      optional match
        (team)<-[:CAPTAIN_OF]-(captain:Person),
        (team)<-[:MEMBER_OF]-(player:Person)
      return team, captain, collect(player) as players
      order by team.name
      """;

    try (Session session = neo.session()) {
      return team(session.run(query, Map.of("hunt", hunt.toString(), "team", team.toString())).single());
    }
  }

  public Team createTeam(UUID hunt, String name) {
    String query = """
      match (hunt:Hunt{uuid:$hunt})
      create
        (team:Team {
          uuid: $team,
          name: $name
        }),
        (team)-[:PLAYED]->(hunt)
      return team 
      """;

    try (Session session = neo.session()) {
      Map<String, Object> params = Map.of(
        "hunt", hunt.toString(),
        "team", UUID.randomUUID().toString(),
        "name", name);

      return team(session.run(query, params).single());
    }
  }

  private Team team(Record record) {
    Team team = new Team();
    team.setId(uuid(record.get("team").get("uuid")));
    team.setName(record.get("team").get("name").asString());
    team.setCaptain(player(record.get("captain")));
    team.setPlayers(players(record.get("players")));
    return team;
  }

  private Player player(Value value) {
    if (value == NullValue.NULL) {
      return null;
    }

    Player player = new Player();
    player.setId(uuid(value.get("uuid")));
    player.setName(value.get("name").asString());
    return player;
  }

  private List<Player> players(Value value) {
    if (value == NullValue.NULL) {
      return null;
    }

    return value.asList(this::player);
  }

  private UUID uuid(Value value) {
    return UUID.fromString(value.asString());
  }
}