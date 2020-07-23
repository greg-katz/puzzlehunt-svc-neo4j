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
  public Optional<TeamSolvedPuzzle> updateSolvedPuzzle(UUID teamId, UUID solvedPuzzleId, TeamSolvedPuzzle newSolvedPuzzle) {
    // Overwriting the changeable properties on the existing object seems like the simplest thing to do when there are only a few of them.
    // If there were a lot of them it'd be simpler to switch things around by setting existing object's relationship properties and ID property
    // on the new object and then replacing the reference in the Team's set with the new instance.
    Team teamInTransaction = teamRepository.findById(teamId).get();
    TeamSolvedPuzzle existingSolvedPuzzle = teamInTransaction.getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getUuid().equals(solvedPuzzleId)).findFirst().get();

    existingSolvedPuzzle.setStart(newSolvedPuzzle.getStart());
    existingSolvedPuzzle.setEnd(newSolvedPuzzle.getEnd());
    existingSolvedPuzzle.setPoints(newSolvedPuzzle.getPoints());

  Team afterSaveTeam = teamRepository.save(teamInTransaction, 1);
    return afterSaveTeam.getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getUuid().equals(solvedPuzzleId)).findFirst();
}
}
