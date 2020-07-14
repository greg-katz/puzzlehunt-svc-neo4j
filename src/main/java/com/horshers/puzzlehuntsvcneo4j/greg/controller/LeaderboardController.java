package com.horshers.puzzlehuntsvcneo4j.greg.controller;

import com.horshers.puzzlehuntsvcneo4j.model.Leaderboard;
import com.horshers.puzzlehuntsvcneo4j.model.TeamResult;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
public class LeaderboardController {

  private static String query = """
    match (hunt:Hunt)<-[played:PLAYED]-(team:Team)-[solved:SOLVED]->(puzzle:Puzzle)
    with hunt.name as huntName, team, solved
    where huntName = $huntName
    call {
      with huntName
      match (hunt:Hunt)-[:HAS]->(puzzle:Puzzle)
      where hunt.name = huntName
      return count(puzzle) as totalPuzzles
    }
    return
      team.name as name,
      count(solved.end) = totalPuzzles as finished,
      sum(solved.points) as score,
      sum(duration.between(solved.start, solved.end)) as time
    order by score desc
    """;

  @GetMapping("/greg/leaderboard")
  public Leaderboard leaderboard() {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));

    Leaderboard leaderboard = new Leaderboard();

    try (Session session = driver.session()) {
      Result result = session.run(query, Map.of("huntName", "DASH 11"));
      leaderboard.setTeamResults(result.stream().map(this::extractTeamResult).collect(toList()));
    }

    return leaderboard;
  }

  private TeamResult extractTeamResult(org.neo4j.driver.Record record) {
    TeamResult teamResult = new TeamResult();
    teamResult.setName(record.get("name").asString());
    teamResult.setFinished(record.get("finished").asBoolean());
    teamResult.setScore(record.get("score").asInt());
    // TODO: Rather than including a Duration object in the TeamResult, we should probably included a human-friendly
    // formatted string
    teamResult.setDuration(Duration.ofSeconds(record.get("time").asIsoDuration().seconds()));

    return teamResult;
  }
}