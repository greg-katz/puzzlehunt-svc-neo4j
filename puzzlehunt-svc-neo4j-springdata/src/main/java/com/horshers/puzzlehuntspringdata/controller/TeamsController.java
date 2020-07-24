package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.model.TeamSolvedPuzzle;
import com.horshers.puzzlehuntspringdata.repo.HuntRepository;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.PuzzleRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamSolvedPuzzleRepository;
import com.horshers.puzzlehuntspringdata.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController("spring-data-teams-controller")
public class TeamsController {

  @Autowired private HuntRepository huntRepository;
  @Autowired private TeamRepository teamRepository;
  @Autowired private TeamSolvedPuzzleRepository teamSolvedPuzzleRepository;
  @Autowired private PersonRepository personRepository;
  @Autowired private PuzzleRepository puzzleRepository;
  @Autowired private TeamService teamService;

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

  // TODO: It would be cool if the team required a captain to be created at the same time
  // TODO: Protect the hunt from accepting a new team with the same name as an existing team
  @PostMapping("/springdata/hunts/{huntId}/teams")
  public Team createTeam(@PathVariable("huntId") Hunt hunt, @RequestBody Team team) {
    if (hunt == null) throw new ResponseStatusException(NOT_FOUND, "Hunt not found");
    if (team.getUuid() != null) throw new ResponseStatusException(BAD_REQUEST, "Team ID must not be supplied (did you mean to update?)");
    return teamService.createTeam(team, hunt.getUuid());
  }

  // TODO: Validate that the provided Team comes with an ID and matches the path variable (any chance that Spring can
  // supplement the entity's annotated validation here by means of an additional validation annotation on the team
  // parameter?)
  // TODO: Validate that a changed team name doesn't collide with an existing team's name
  @PutMapping("/springdata/teams/{id}")
  public Team updateTeam(@RequestBody Team team) {
    return teamRepository.save(team);
  }

  // TODO: Spring is returning a 200 regardless of whether the team to be deleted exists or not. That seems a bit
  // uninformative.
  @DeleteMapping("/springdata/teams/{id}")
  public void deleteTeam(@PathVariable UUID id) {
    teamRepository.deleteById(id);
  }

  @GetMapping("/springdata/teams/{id}/captain")
  public Person findCaptain(@PathVariable("id") Team team) {
    return team.getCaptain();
  }

  // TODO: Validate that the captain exists
  // TODO: Implement business rule: If the incoming captain is already on another team, remove them from that team
  @PutMapping("/springdata/teams/{id}/captain")
  public Person setCaptain(@PathVariable("id") Team team, @RequestBody UUID captainId) {
    Person captain = personRepository.findById(captainId).get();
    team.setCaptain(captain);
    team.getPlayers().add(captain);
    return teamRepository.save(team).getCaptain();
  }

  // TODO: Implement business rule: When the captain is deleted, make one of the other players on the team the captain
  @DeleteMapping("/springdata/teams/{id}/captain")
  public Team deleteCaptain(@PathVariable("id") Team team) {
    Person oldCaptain = team.getCaptain();

    // When the captain resigns, if the team has any other players, promote one of them to be the captain
    Person newCaptain = team.getPlayers().stream()
      .filter(p -> !p.getUuid().equals(oldCaptain.getUuid()))
      .findFirst()
      .orElse(null);

    team.setCaptain(newCaptain);

    return teamRepository.save(team);
  }

  // TODO: Turn a null team into a 404 (any chance an @NonNull annotation can do the trick?)
  @GetMapping("/springdata/teams/{id}/players")
  public List<Person> findPlayers(@PathVariable("id") Team team) {
    return team.getPlayers();
  }

  // TODO: Should we validate that the players exist?
  // Add persons 3 and 4 to team 1:
  // curl -X POST -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players --data '["9434d65c-a693-4dac-95fd-5496ebd32650", "5efd7a47-98f7-4c4d-87e9-ba7434c4afa6"]'
  @PostMapping("/springdata/teams/{teamId}/players")
  public List<Person> addPlayers(@PathVariable("teamId") UUID teamId, @RequestBody List<UUID> playerIds) {
    return teamService.addPlayers(teamId, playerIds);
  }

  // TODO: Should we validate that the player exists?
  // TODO: If this argument resolution magic works, is the player inside the team reference-equal to the player param?
  // If it isn't reference-equal, does it become reference-equal by adding @Transaction to this method?
  // Delete person 4 from team 1:
  // curl -X DELETE -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players/5efd7a47-98f7-4c4d-87e9-ba7434c4afa6
  @DeleteMapping("/springdata/teams/{teamId}/players/{playerId}")
  public List<Person> deletePlayer(@PathVariable("teamId") UUID teamId, @PathVariable("playerId") UUID playerId) {
    return teamService.deletePlayer(teamId, playerId);
  }

  // TODO: Is there a way to get Spring's argument resolution to populate a List<Person> instead of List<UUID>?
  // Set team 1's players to person 3 and person 4:
  // curl -X PUT -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players --data '["9434d65c-a693-4dac-95fd-5496ebd32650", "5efd7a47-98f7-4c4d-87e9-ba7434c4afa6"]'
  @PutMapping("/springdata/teams/{id}/players")
  public List<Person> setPlayers(@PathVariable("id") UUID teamId, @RequestBody List<UUID> players) {
    return teamService.setPlayers(teamId, players);
  }

  // TODO: Validate that the team exists
  // Get solved puzzled of Team 1:
  // curl -X GET -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles
  @GetMapping("/springdata/teams/{id}/solvedpuzzles")
  public List<TeamSolvedPuzzle> findSolvedPuzzles(@PathVariable UUID id) {
    return teamRepository.findById(id).get().getTeamSolvedPuzzles();
  }

  // TODO: What does returning Optional do, exactly? Does Spring MVC do something cool when the Optional is empty?
  // Create a solved relationship between Team 1 and the "lungs" puzzle:
  // curl -X POST -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles --data '"9e03b12a-dc65-485f-af28-9c5251a5c6f5"'
  @PostMapping("/springdata/teams/{id}/solvedpuzzles")
  public Optional<TeamSolvedPuzzle> createSolvedPuzzle(@PathVariable("id") Team team, @RequestBody UUID puzzleId) {
    TeamSolvedPuzzle solvedPuzzle = new TeamSolvedPuzzle();
    solvedPuzzle.setStart(ZonedDateTime.now());
    solvedPuzzle.setPuzzle(puzzleRepository.findById(puzzleId).get());
    solvedPuzzle.setTeam(team);
    team.getTeamSolvedPuzzles().add(solvedPuzzle);
    // TODO: Refactor to be less of a jerk
    return teamRepository.save(team).getTeamSolvedPuzzles().stream().filter(tsp -> tsp.getPuzzle().getUuid().equals(puzzleId)).findFirst();
  }

  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  // TODO: Also validate that the SolvedPuzzle ID is valid/existing in the current team.
  // Update the TeamSolvedPuzzle between Team 1 and the hearts puzzle with some new dates and points values:
  // curl -X PUT -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles/214941d3-98d8-4378-b9f1-c69490e59e26 --data '{"uuid":"214941d3-98d8-4378-b9f1-c69490e59e26","start":"2015-06-24T09:32:01.001+01:00","end":"2015-06-24T11:52:01.001+01:00","points":"551"}'
  @PutMapping("/springdata/teams/{teamId}/solvedpuzzles/{oldSolvedPuzzle}")
  public TeamSolvedPuzzle updateSolvedPuzzle(@PathVariable("teamId") UUID teamId, @PathVariable("oldSolvedPuzzle") TeamSolvedPuzzle oldSolvedPuzzle, @RequestBody TeamSolvedPuzzle newSolvedPuzzle) {
    // TODO: Write HORSHERS comment!
    oldSolvedPuzzle.setStart(newSolvedPuzzle.getStart());
    oldSolvedPuzzle.setEnd(newSolvedPuzzle.getEnd());
    oldSolvedPuzzle.setPoints(newSolvedPuzzle.getPoints());
    return teamSolvedPuzzleRepository.save(oldSolvedPuzzle);
  }

  // TODO: Either validate that the team and the solved puzzle belong together or get rid of nesting under /team
  @DeleteMapping("/springdata/teams/{teamId}/solvedpuzzles/{solvedPuzzleId}")
  // Delete the TeamSolvedPuzzle between Team 1 and the hearts puzzle:
  // curl -X DELETE -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles/214941d3-98d8-4378-b9f1-c69490e59e26
  public void deleteSolvedPuzzle(@PathVariable UUID solvedPuzzleId) {
    teamSolvedPuzzleRepository.deleteById(solvedPuzzleId);
  }
}