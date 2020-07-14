package com.horshers.puzzlehuntsvcneo4j.mike.dao;

import com.horshers.puzzlehuntsvcneo4j.model.Leaderboard;
import com.horshers.puzzlehuntsvcneo4j.model.TeamResult;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.IsoDuration;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LeaderboardDao {

  private final Driver neo;

  LeaderboardDao() {
    neo = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }

  public Leaderboard getLeaderboard() {
    Map<String, Object> params = new HashMap<>();
    params.put("huntName", "DASH 11");
    try (Session session = neo.session()) {
      Result result = session.run("match (hunt:Hunt)<-[played:PLAYED]-(team:Team)-[solved:SOLVED]->(puzzle:Puzzle)\n" +
          "with hunt.name as huntName, team, solved\n" +
          "where huntName = $huntName\n" +
          "call {\n" +
          "  with huntName\n" +
          "  match (hunt:Hunt)-[:HAS]->(puzzle:Puzzle)\n" +
          "  where hunt.name = huntName\n" +
          "  return count(puzzle) as totalPuzzles\n" +
          "}\n" +
          "return\n" +
          "  team.name as teamName,\n" +
          "  count(solved.end) = totalPuzzles as finishedHunt,\n" +
          "  sum(solved.points) as score,\n" +
          "  duration.between(min(solved.start), max(solved.end)) as huntDuration,\n" +
          "  sum(duration.between(solved.start, solved.end)) as solvingDuration\n" +
          "order by score desc", params);
      return makeLeaderboardFromRecords(result.list());
    }
  }

  private Leaderboard makeLeaderboardFromRecords(List<Record> records) {
    Leaderboard leaderboard = new Leaderboard();
    List<TeamResult> teamResults = new ArrayList<>();
    for (Record record : records) {
      teamResults.add(makeTeamResult(record));
    }
    leaderboard.setTeamResults(teamResults);
    return leaderboard;
  }

  private TeamResult makeTeamResult(Record record) {
    TeamResult teamResult = new TeamResult();
    teamResult.setName(record.get("teamName").asString());
    teamResult.setScore(record.get("score").asInt());
    teamResult.setFinished(record.get("finishedHunt").asBoolean());
    teamResult.setTime(convertIsoDuration(record.get("solvingDuration").asIsoDuration()));
    return teamResult;
  }

  private String convertIsoDuration(IsoDuration isoDuration) {
    Duration time = Duration.ofSeconds(isoDuration.seconds(), isoDuration.nanoseconds());
    return String.format("%d:%02d:%02d", time.toHours(), time.toMinutes(), time.toSeconds());
  }
}
