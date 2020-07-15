package com.horshers.puzzlehunt.driver.dao;

import com.horshers.puzzlehunt.driver.model.Leaderboard;
import com.horshers.puzzlehunt.driver.model.TeamResult;
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

  String query =
    """
      match (hunt:Hunt)<-[played:PLAYED]-(team:Team)-[solved:SOLVED]->(puzzle:Puzzle)
      with hunt.name as huntName, team, solved
      where huntName = 'DASH 11'
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

  private final Driver neo;

  LeaderboardDao() {
    neo = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }

  public Leaderboard getLeaderboard() {
    Map<String, Object> params = new HashMap<>();
    params.put("huntName", "DASH 11");
    try (Session session = neo.session()) {
      Result result = session.run(query, params);
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
    teamResult.setName(record.get("name").asString());
    teamResult.setScore(record.get("score").asInt());
    teamResult.setFinished(record.get("finished").asBoolean());
    teamResult.setTime(convertIsoDuration(record.get("time").asIsoDuration()));
    return teamResult;
  }

  private String convertIsoDuration(IsoDuration isoDuration) {
    Duration time = Duration.ofSeconds(isoDuration.seconds(), isoDuration.nanoseconds());
    return String.format("%d:%02d:%02d", time.toHoursPart(), time.toMinutesPart(), time.toSecondsPart());
  }
}
