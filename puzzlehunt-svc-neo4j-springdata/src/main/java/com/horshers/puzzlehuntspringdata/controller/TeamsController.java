package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.PuzzleRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.collections4.IterableUtils.toList;

@RestController("spring-data-teams-controller")
public class TeamsController {

  @Autowired private TeamRepository teamRepository;
  @Autowired private PersonRepository personRepository;
  @Autowired private PuzzleRepository puzzleRepository;

  @GetMapping("/springdata/teams")
  public List<Team> findAllTeams() {
    // TODO: How do you look up the teams for the hunt? Do you need to look up the Hunt object first so you can pass
    //  it in? Can the repository define a query with @Query that takes a hunt UUID?
    return toList(teamRepository.findAll());
  }

  // TODO: Does the default depth of one include the TeamSolvedPuzzles including their Puzzles?
  @GetMapping("/springdata/teams/{id}")
  public Team findTeam(@PathVariable("id") Team team) {
    return team;
  }

  // TODO: Validate that the ID is null
  // TODO: It would be cool if the team required a captain to be created at the same time
  @PostMapping("/springdata/teams")
  public Team createTeam(Team team) {
    return teamRepository.save(team);
  }

  // TODO: Validate that the provided Team comes with an ID and matches the path variable (any chance that Spring can supplement the entity's
  // annotated validation here by means of an additional validation annotation on the team parameter?)
  @PutMapping("/springdata/teams/{id}")
  public Team updateTeam(Team team) {
    return teamRepository.save(team);
  }

  // TODO: What response code does this return on successful delete? What does it return when you delete a non-existent
  // team?
  @DeleteMapping("/springdata/teams/{id}")
  public void deleteTeam(@PathVariable UUID id) {
    teamRepository.deleteById(id);
  }

  // TODO: Validate that the captain exists
  @PutMapping("/springdata/teams/{id}/captain")
  public Person setCaptain(@PathVariable("id") Team team, UUID captainId) {
    Person captain = personRepository.findById(captainId).get();
    team.setCaptain(captain);
    team.getPlayers().add(captain);
    return teamRepository.save(team).getCaptain();
  }

  // TODO: Turn a null team into a 404 (any chance an @NonNull annotation can do the trick?)
  @GetMapping("/springdata/teams/{id}/players")
  public List<Person> findPlayers(@PathVariable("id") Team team) {
    return team.getPlayers();
  }

  // TODO: Should we validate that the players exist?
  @PostMapping("/springdata/teams/{id}/players")
  public List<Person> addPlayers(@PathVariable("id") Team team, List<UUID> players) {
    team.getPlayers().addAll(toList(personRepository.findAllById(players)));
    return teamRepository.save(team).getPlayers();
  }

  // TODO: Should we validate that the player exists?
  // TODO: If this argument resolution magic works, is the player inside the team reference-equal to the player param?
  // If it isn't reference-equal, does it become reference-equal by adding @Transaction to this method?
  @DeleteMapping("/springdata/teams/{teamId}/players/{playerId}")
  public List<Person> deletePlayer(@PathVariable("teamId") Team team, @PathVariable("playerId") Person player) {
    team.getPlayers().remove(player);
    return teamRepository.save(team).getPlayers();
  }

  // TODO: Is there a way to get Spring's argument resolution to populate a List<Person> instead of List<UUID>?
  @PutMapping("/springdata/teams/{id}/players")
  public List<Person> setPlayers(@PathVariable("id") Team team, List<UUID> players) {
    team.setPlayers(toList(personRepository.findAllById(players)));
    return teamRepository.save(team).getPlayers();
  }

  // TODO: Validate that the team exists
  // TODO: Does the default depth of one include the TeamSolvedPuzzles including their Puzzles?
  @GetMapping("/springdata/teams/{id}/solvedpuzzles")
  public List<TeamSolvedPuzzle> findSolvedPuzzles(@PathVariable UUID id) {
    return teamRepository.findById(id, 2).get().getTeamSolvedPuzzles();
  }

  @PostMapping("/springdata/teams/{id}/solvedpuzzles")
  public Optional<TeamSolvedPuzzle> createSolvedPuzzle(@PathVariable("id") Team team, UUID puzzle) {
    TeamSolvedPuzzle solvedPuzzle = new TeamSolvedPuzzle();
    solvedPuzzle.setStart(LocalTime.now());
    solvedPuzzle.setPuzzle(puzzleRepository.findById(puzzle).get());
    team.getTeamSolvedPuzzles().add(solvedPuzzle);
    // TODO: Refactor to be less of a jerk
    return teamRepository.save(team).getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getPuzzle().getUuid().equals(puzzle)).findFirst();
  }

  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  @PutMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId")
  public Optional<TeamSolvedPuzzle> updateSolvedPuzzle(@PathVariable("teamId") Team team, TeamSolvedPuzzle solvedPuzzle) {
    // Nani???
    return null;
  }

  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  @DeleteMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId")
  public void deleteSolvedPuzzle(@PathVariable UUID solvedPuzzleId) {
    // Nani???
  }
}