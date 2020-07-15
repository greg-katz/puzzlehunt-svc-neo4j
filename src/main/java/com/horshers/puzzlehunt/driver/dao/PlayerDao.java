package com.horshers.puzzlehunt.driver.dao;

import com.horshers.puzzlehunt.driver.model.Player;
import com.horshers.puzzlehunt.driver.model.Team;
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
        CREATE
        (person:Person {
          uuid: $playerId,
          name: $name
        }),
        (person)-[:MEMBER_OF]->(team:Team{uuid:$teamId})
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
