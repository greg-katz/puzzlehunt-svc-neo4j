package com.horshers.puzzlehuntdriver.dao;

import com.horshers.puzzlehuntdriver.model.Player;
import com.horshers.puzzlehuntdriver.model.Team;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.internal.value.NullValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class PlayerDao {

  @Autowired
  Driver neoDriver;

  public Player createPlayer(UUID teamId, String name) {
    UUID playerId = UUID.randomUUID();
    String query =
      """
        MATCH (team:Team{uuid:$teamId})
        CREATE
        (person:Person {
          uuid: $playerId,
          name: $name
        }),
        (person)-[:MEMBER_OF]->(team)
        RETURN person
      """;
    Map<String, Object> params = Map.of("teamId", teamId.toString(), "playerId", playerId.toString(), "name", name);
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return makePlayerFromResult(result.single());
    }
  }

  public Player readPlayer(UUID playerId) {
    String query =
      """
        MATCH (person:Person{uuid:$uuid})-[:MEMBER_OF]-(team:Team)
        RETURN person, team
      """;
    Map<String, Object> params = Map.of("uuid", playerId.toString());
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return makePlayerFromResult(result.single());
    }
  }

  public Player updatePlayer(UUID playerId, String name) {
    String query =
      """
        MATCH (person:Person{uuid:$uuid})
        SET person.name = $name
        RETURN person
      """;
    Map<String, Object> params = Map.of("uuid", playerId.toString(), "name", name);
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return makePlayerFromResult(result.single());
    }
  }

  public boolean removePlayer(UUID playerId) {
    String query =
        """
          MATCH (person:Person{uuid:$uuid})
          DETACH DELETE person
          RETURN count(person) as count
        """;
    Map<String, Object> params = Map.of("uuid", playerId.toString());
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return result.single().get("count").asInt() >= 1;
    }
  }

  public Player changeTeam(UUID playerId, UUID newTeamId) {
    String query =
        """
          MATCH (person:Person{uuid:$playerId})-[r:MEMBER_OF]-(firstTeam:Team)
          DELETE r
          WITH person
          MATCH (secondTeam:Team{uuid:$newTeamId})
          CREATE (person)-[:MEMBER_OF]->(secondTeam)
          RETURN person, secondTeam as team
        """;
    Map<String, Object> params = Map.of("playerId", playerId.toString(), "newTeamId", newTeamId.toString());
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return makePlayerFromResult(result.single());
    }
  }

  private Player makePlayerFromResult(Record record) {
    Player player = new Player();
    player.setId(UUID.fromString(record.get("person").get("uuid").asString()));
    player.setName(record.get("person").get("name").asString());
    player.setTeam(makeShallowTeamFromRecord(record));
    return player;
  }

  private Team makeShallowTeamFromRecord(Record record) {
    if (record.get("team") == NullValue.NULL) {
      return null;
    }
    Team team = new Team();
    team.setId(UUID.fromString(record.get("team").get("uuid").asString()));
    team.setName(record.get("team").get("name").asString());
    return team;
  }
}
