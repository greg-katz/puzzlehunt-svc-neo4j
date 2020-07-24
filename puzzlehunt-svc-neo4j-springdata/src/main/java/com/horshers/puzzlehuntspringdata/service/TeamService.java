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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.IterableUtils.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class TeamService {

  @Autowired HuntRepository huntRepository;
  @Autowired PersonRepository personRepository;
  @Autowired PuzzleRepository puzzleRepository;
  @Autowired TeamRepository teamRepository;

  @Transactional
  public Team createTeam(Team team, UUID huntId) {
    Hunt hunt = huntRepository.findById(huntId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hunt not found"));
    team = teamRepository.save(team);
    hunt.addTeam(team);
    hunt = huntRepository.save(hunt);
    return hunt.findTeam(team).orElseThrow(() -> new RuntimeException("Should never happen!"));
  }

  @Transactional
  public Person setCaptain(Team team, UUID captainId) {
    Person captain = personRepository.findById(captainId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Captain not found"));
    team.setCaptain(captain); // TODO: So if the team already had a captain, does this result in two CAPTAIN_OF relationships in the database?
    team.getPlayers().add(captain); // TODO: NPE (should you override Lombok's setCaptain method to also (null safely) add to the players list?
    return teamRepository.save(team).getCaptain();
  }

  @Transactional
  public Team removeCaptain(Team team) {
    Person oldCaptain = team.getCaptain();

    // When the captain resigns, if the team has any other players, promote one of them to be the captain
    Person newCaptain = team.getPlayers().stream()
      .filter(p -> !p.getUuid().equals(oldCaptain.getUuid()))
      .findFirst()
      .orElse(null);

    team.setCaptain(newCaptain);

    return teamRepository.save(team);
  }

  public List<Person> addPlayers(UUID teamId, List<UUID> playerIds) {
    Team team = teamRepository.findById(teamId).get();
    List<Person> players = toList(personRepository.findAllById(playerIds));
    team.getPlayers().addAll(players);

    return teamRepository.save(team).getPlayers();
  }

  public List<Person> deletePlayer(UUID teamId, UUID playerId) {
    Team team = teamRepository.findById(teamId).get();
    team.setPlayers(team.getPlayers().stream().filter(player -> !player.getUuid().equals(playerId)).collect(Collectors.toList()));
    return teamRepository.save(team).getPlayers();
  }

  public List<Person> setPlayers(UUID teamId, List<UUID> playerIds) {
    Team team = teamRepository.findById(teamId).get();
    Iterable<Person> players = personRepository.findAllById(playerIds);

    team.setPlayers(toList(players));

    return teamRepository.save(team).getPlayers();
  }

  @Transactional
  public TeamSolvedPuzzle createSolvedPuzzle(Team team, UUID puzzleId) {
    Puzzle puzzle = puzzleRepository.findById(puzzleId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Puzzle not found"));
    TeamSolvedPuzzle solvedPuzzle = new TeamSolvedPuzzle();
    solvedPuzzle.setStart(ZonedDateTime.now());
    solvedPuzzle.setTeam(team);
    solvedPuzzle.setPuzzle(puzzle);
    team.addSolvedPuzzle(solvedPuzzle); // TODO: What if the team already has a TSP for this puzzle?
    return teamRepository.save(team).findSolvedPuzzle(puzzleId).orElseThrow(() -> new RuntimeException("Should never happen!"));
  }
}