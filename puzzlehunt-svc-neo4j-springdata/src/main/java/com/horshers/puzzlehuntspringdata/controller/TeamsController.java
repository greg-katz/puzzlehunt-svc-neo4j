package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import com.horshers.puzzlehuntspringdata.repo.HuntRepository;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.PuzzleRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import com.horshers.puzzlehuntspringdata.service.TeamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.collections4.IterableUtils.toList;

@RestController("spring-data-teams-controller")
public class TeamsController {

  @Autowired private HuntRepository huntRepository;
  @Autowired private TeamRepository teamRepository;
  @Autowired private PersonRepository personRepository;
  @Autowired private PuzzleRepository puzzleRepository;
  @Autowired private TeamsService teamsService;

  // TODO: Deal with a non-existent hunt
  // TODO: How do you get the teams to be sorted by name? Can you convince Neo to load the hunt's teams in alphabetical
  // order?
  @GetMapping("/springdata/hunts/{id}/teams")
  public List<Team> findAllTeams(@PathVariable("id") Hunt hunt) {
    return hunt.getTeams();
  }

  @GetMapping("/springdata/teams/{id}")
  public Team findTeam(@PathVariable("id") Team team) {
    return team;
  }

  // TODO: Validate that the ID is null
  // TODO: It would be cool if the team required a captain to be created at the same time
  // TODO: Protect the hunt from accepting a new team with the same name as an existing team
  @PostMapping("/springdata/hunts/{id}/teams")
  public Team createTeam(@PathVariable("id") Hunt hunt, @RequestBody Team team) {
    team = teamRepository.save(team);
    hunt.getTeams().add(team);
    // TODO: Refactor to be less of a jerk
    UUID teamId = team.getUuid();
    return huntRepository.save(hunt).getTeams().stream().filter(t -> t.getUuid().equals(teamId)).findFirst().get();
  }

  // TODO: Validate that the provided Team comes with an ID and matches the path variable (any chance that Spring can
  // supplement the entity's annotated validation here by means of an additional validation annotation on the team
  // parameter?)
  // TODO: Validate that a changed team name doesn't collide with an existing team's name
  @PutMapping("/springdata/teams/{id}")
  public Team updateTeam(@RequestBody Team team) {
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
    return teamRepository.findById(id).get().getTeamSolvedPuzzles();
  }

  // TODO: What does returning Optional do, exactly? Does Spring MVC do something cool when the Optional is empty?
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
  // Also validate that the SolvedPuzzle ID is valid/existing in the current team.
  @PutMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId}")
  @Transactional
  public Optional<TeamSolvedPuzzle> updateSolvedPuzzle(@PathVariable("teamId") Team team, @PathVariable("solvedPuzzleId") UUID solvedPuzzleId, @RequestBody TeamSolvedPuzzle newSolvedPuzzle) {
    return teamsService.updateSolvedPuzzle(team, solvedPuzzleId, newSolvedPuzzle);
  }

/*  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  @PutMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId}")
  public Optional<TeamSolvedPuzzle> updateSolvedPuzzle(@PathVariable("teamId") Team team, @RequestParam MultiValueMap<String, String> solvedPuzzle) {
    // Nani???
    return null;
  }*/

  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  @DeleteMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId}")
  public void deleteSolvedPuzzle(@PathVariable UUID solvedPuzzleId) {
    // Nani???
  }
}