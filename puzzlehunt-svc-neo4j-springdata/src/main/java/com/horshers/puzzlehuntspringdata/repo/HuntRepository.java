package com.horshers.puzzlehuntspringdata.repo;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Leaderboard;
import com.horshers.puzzlehuntspringdata.model.TeamResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface HuntRepository extends Neo4jRepository<Hunt, UUID> {

  Hunt findByUuid(UUID uuid);

  Hunt findByName(String name);

  @Query("""
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
    """)
  List<TeamResult> getTeamResultsByHuntName(@Param("huntName") String huntName);

  default Leaderboard getLeaderboardByHuntName(String huntName) {
    Leaderboard leaderboard = new Leaderboard();
    leaderboard.setTeamResults(getTeamResultsByHuntName(huntName));
    return leaderboard;
  }
}