package com.horshers.puzzlehuntspringdata.service;

import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class TeamsService {

  @Autowired TeamRepository teamRepository;

  @Transactional
  public Optional<TeamSolvedPuzzle> updateSolvedPuzzle(Team team, UUID solvedPuzzleId, TeamSolvedPuzzle newSolvedPuzzle) {
    // Overwriting the changeable properties on the existing object seems like the simplest thing to do when there are only a few of them.
    // If there were a lot of them it'd be simpler to switch things around by setting existing object's relationship properties and ID property
    // on the new object and then replacing the reference in the Team's set with the new instance.
    TeamSolvedPuzzle existingSolvedPuzzle = team.getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getUuid().equals(solvedPuzzleId)).findFirst().get();

    team.getTeamSolvedPuzzles().remove(existingSolvedPuzzle);

    newSolvedPuzzle.setUuid(existingSolvedPuzzle.getUuid());
    newSolvedPuzzle.setTeam(existingSolvedPuzzle.getTeam());
    newSolvedPuzzle.setPuzzle(existingSolvedPuzzle.getPuzzle());
    team.getTeamSolvedPuzzles().add(newSolvedPuzzle);

/*    existingSolvedPuzzle.setStart(newSolvedPuzzle.getStart());
    existingSolvedPuzzle.setEnd(newSolvedPuzzle.getEnd());*/

    Team afterSaveTeam = teamRepository.save(team, 1);
    return afterSaveTeam.getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getUuid().equals(solvedPuzzleId)).findFirst();
  }
}
