package com.horshers.puzzlehuntspringdata.service;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Puzzle;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import com.horshers.puzzlehuntspringdata.repo.HuntRepository;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.PuzzleRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamSolvedPuzzleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections4.IterableUtils.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class TeamService {

  @Autowired HuntRepository huntRepository;
  @Autowired PersonRepository personRepository;
  @Autowired PuzzleRepository puzzleRepository;
  @Autowired TeamRepository teamRepository;
  @Autowired TeamSolvedPuzzleRepository teamSolvedPuzzleRepository;

  public Team createTeam(Team team, UUID huntId) {
    Hunt hunt = huntRepository.findById(huntId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hunt not found"));
    team = teamRepository.save(team);
    hunt.addTeam(team);
    hunt = huntRepository.save(hunt);
    return hunt.findTeam(team).orElseThrow(() -> new RuntimeException("Should never happen!"));
  }

  public Person setCaptain(Team team, UUID captainId) {
    Person captain = personRepository.findById(captainId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Captain not found"));
    team.setCaptain(captain); // TODO: So if the team already had a captain, does this result in two CAPTAIN_OF relationships in the database?
    team.getPlayers().add(captain); // TODO: NPE (should you override Lombok's setCaptain method to also (null safely) add to the players list?
    return teamRepository.save(team).getCaptain();
  }

  public Team removeCaptain(Team team) {
    Person oldCaptain = team.getCaptain();

    // When the captain resigns, if the team has any other players, promote one of them to be the captain
    Person newCaptain = team.getPlayers().stream()
      .filter(p -> !p.getId().equals(oldCaptain.getId()))
      .findFirst()
      .orElse(null);

    team.setCaptain(newCaptain);

    return teamRepository.save(team);
  }

  public List<Person> addPlayers(Team team, List<UUID> playerIds) {
    List<Person> players = toList(personRepository.findAllById(playerIds));
    team.getPlayers().addAll(players);

    return teamRepository.save(team).getPlayers();
  }

  public List<Person> deletePlayer(Team team, Person player) {
    team.getPlayers().remove(player);
    return teamRepository.save(team).getPlayers();
  }

  public List<Person> setPlayers(Team team, List<UUID> playerIds) {
    Iterable<Person> players = personRepository.findAllById(playerIds);
    team.setPlayers(toList(players));

    return teamRepository.save(team).getPlayers();
  }

  public TeamSolvedPuzzle createSolvedPuzzle(Team team, Puzzle puzzle) {
    TeamSolvedPuzzle solvedPuzzle = new TeamSolvedPuzzle();
    solvedPuzzle.setStart(ZonedDateTime.now());
    solvedPuzzle.setTeam(team);
    solvedPuzzle.setPuzzle(puzzle);
    team.addSolvedPuzzle(solvedPuzzle); // TODO: What if the team already has a TSP for this puzzle?
    return teamRepository.save(team).findSolvedPuzzle(puzzle).orElseThrow(() -> new RuntimeException("Should never happen!"));
  }

  /* There's a question worth considering here on whether it's better to set properties on the oldSolvedPuzzle from
     the newSolvedPuzzle or the other way around. On their way in, "oldSolvedPuzzle" is the TeamSolvedPuzzle instance
     that we pulled from the database and "newSolvedPuzzle" contains the relationship attributes we want to update
     coming from the request.

     The advantage of setting properties on the oldSolvedPuzzle is that it's already wired into the object graph that
     contains its relationships to Team and Puzzle (and circularly from Team back to itself). You just set the mutable
     properties based on the newSolvedPuzzle, save the oldSolvedPuzzle, and you're done.

     Setting properties on the newSolvedPuzzle is a bit more complex but arguably scales better and is more maintainable
     since if you add a new relationship property this code won't need to change. You need to set the Team and Puzzle
     fields in the newSolvedPuzzle before saving, which makes sense, but the real trick is that you also need to replace
     the circular reference of TeamSolvedPuzzle->Team->TeamSolvedPuzzle to be the newSolvedPuzzle instance, or else
     the oldSolvedPuzzle instance will still be in the object graph that's being saved and the save won't work as you
     expect. */
  public TeamSolvedPuzzle updateSolvedPuzzle(TeamSolvedPuzzle oldSolvedPuzzle, TeamSolvedPuzzle newSolvedPuzzle) {


   /*
     What the code would look like if we saved the oldSolvedPuzzle instance instad of newSolvedPuzzle:

      oldSolvedPuzzle.setStart(newSolvedPuzzle.getStart());
      oldSolvedPuzzle.setEnd(newSolvedPuzzle.getEnd());
      oldSolvedPuzzle.setPoints(newSolvedPuzzle.getPoints());

      return teamSolvedPuzzleRepository.save(oldSolvedPuzzle);
    */

    Team team = oldSolvedPuzzle.getTeam();
    team.getTeamSolvedPuzzles().remove(oldSolvedPuzzle);
    team.getTeamSolvedPuzzles().add(newSolvedPuzzle);

    newSolvedPuzzle.setTeam(team);
    newSolvedPuzzle.setPuzzle(oldSolvedPuzzle.getPuzzle());
    newSolvedPuzzle.setId(oldSolvedPuzzle.getId());

    return teamSolvedPuzzleRepository.save(newSolvedPuzzle);
  }
}