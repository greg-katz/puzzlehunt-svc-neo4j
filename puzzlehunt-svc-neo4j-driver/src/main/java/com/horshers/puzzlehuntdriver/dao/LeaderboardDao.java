package com.horshers.puzzlehuntdriver.dao;

import com.horshers.puzzlehuntdriver.model.Leaderboard;
import com.horshers.puzzlehuntdriver.model.TeamResult;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.IsoDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class LeaderboardDao {

  @Autowired
  private Driver neoDriver;

  String query =
    """
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

  public Leaderboard getLeaderboard() {
    Map<String, Object> params = Map.of("huntName", "DASH 11");
    try (Session session = neoDriver.session()) {
      Result result = session.run(query, params);
      return makeLeaderboardFromRecords(result.list());
    }
  }

  private Leaderboard makeLeaderboardFromRecords(List<Record> records) {
    Leaderboard leaderboard = new Leaderboard();
    leaderboard.setTeamResults(records.stream().map(this::makeTeamResult).collect(toList()));
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