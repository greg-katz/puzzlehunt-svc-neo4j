package com.horshers.puzzlehuntspringdata.controller;

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
import com.horshers.puzzlehuntspringdata.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

  /*
    Random musings about things we decided while writing this:

    1. In our data model teams are owned by hunts. That leads pretty naturally to at least some of these endpoints
    being nested under /hunts/{huntId}, because the operation you're doing on the team is in the context of that hunt.
    Create team is a good example of this - we model the hunt's team's collection at /hunts/{huntId}/teams and do a POST
    to create a new team for that hunt. Very nice and very RESTful.

    But what's not as clear is what to do endpoints that update or delete a team - ones where the team already exists.
    Team objects may be conceptually owned by hunts but they still have unique database IDs. If you made the rule that
    all team endpoints should be nested under their hunts you'd wind up with a lot of huntId path variables that don't
    even get used in their endpoints - or if they do get used it would only be to validate that it's the correct huntId
    for that team, a problem you wouldn't have if the team endpoint weren't nested under the hunt in the first place.
    This always-nesting idea also seems dubious when you start thinking about a deeper hierarchy, if our data model had entities that are
    owned by the team and by proxy also the hunt.

    What we've settled on here as a debatable compromise is:
    1. Nest the endpoint under the parent if it obviously makes sense to do so (e.g. find the teams for this hunt,
    create a team in this hunt - any operation that is about teams but that doesn't involve passing around teamIds.)
    2. Don't nest the endpoint under the parent otherwise. If it's an operation about a specific Team, for example, it
    can just be at /teams/{teamId}.

    A side note here is that we did choose to keep the methods for the related TeamSolvedPuzzle relationship entity here
    even though it has its own repository and therefore could have its own controller/service. The argument is that
    conceptually the TeamSolvedPuzzle actions are still about updating the team, and that the object itself is a bit
    of a goofy concept that doesn't stand on its own and therefore doesn't deserve full top-level treatment. But
    TeamSolvedPuzzles also have unique IDs and we still chose not to nest their endpoint under team for the actions that
    refer to a TeamSolvedPuzzle ID, so they do wind up with a few top-level REST endpoints. This is another debatable
    area. */

  // TODO: How do you get the teams to be sorted by name? Can you convince Neo to load the hunt's teams in alphabetical
  // order?
  @GetMapping("/springdata/hunts/{id}/teams")
  public List<Team> findAllTeams(@PathVariable("id") Hunt hunt) {
    // TODO: This null check will never be called because the DomainClassConverter unfortunately returns the id
    // path variable's value as a string in the event that the identified hunt doesn't exist, instead of returning
    // null. This causes an IllegalStateException to be thrown before the body of this method is called.
    // HORSHERS ALERT: Mike *didn't* see this behavior consistently. Speculation: This *might* be due to a cache of
    // converters in GenericConversionService. DomainClassConverter has an inner converter class that returns null when
    // the domain object is not found. Mike observed that, when he saw this method be entered with a null hunt arg,
    // a breakpoint in the DomainClassConverter's outer convert method *didn't* get hit. Greg wasn't able to repro.
    // Aside from the mystery of why the same code behaved differently on two different machines, why does the
    // DomainClassConverter *want* to return the passed-in string? Shouldn't it return the null that its inner
    // converter returned?
    //if (hunt == null) throw new ResponseStatusException(NOT_FOUND, "Hunt not found");
    return hunt.getTeams();
  }

  @GetMapping("/springdata/teams/{id}")
  public Team findTeam(@PathVariable("id") Team team) {
    return team;
  }

  // TODO: It would be cool if the team required a captain to be created at the same time
  // TODO: Protect the hunt from accepting a new team with the same name as an existing team (note that team name
  // uniqueness *across* hunts is not guaranteed, so enforcing this rule isn't as simple as adding a Neo4j uniqueness
  // constraint on the name property)
  @PostMapping("/springdata/hunts/{huntId}/teams")
  public Team createTeam(@PathVariable("huntId") Hunt hunt, @RequestBody Team team) {
    if (hunt == null) throw new ResponseStatusException(NOT_FOUND, "Hunt not found");
    if (team.getId() != null) throw new ResponseStatusException(BAD_REQUEST, "Team ID must not be supplied (did you mean to update?)");
    return teamService.createTeam(team, hunt.getId());
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
    return teamService.setCaptain(team, captainId);
  }

  // TODO: Implement business rule: When the captain is deleted, make one of the other players on the team the captain
  @DeleteMapping("/springdata/teams/{id}/captain")
  public Team removeCaptain(@PathVariable("id") Team team) {
    return teamService.removeCaptain(team);
  }

  // TODO: Turn a null team into a 404 (any chance an @NonNull annotation can do the trick?)
  @GetMapping("/springdata/teams/{id}/players")
  public List<Person> findPlayers(@PathVariable("id") Team team) {
    return team.getPlayers();
  }

  // TODO: Should we validate that the players exist?
  // Add persons 3 and 4 to team 1:
  // curl -s -X POST -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players --data '["9434d65c-a693-4dac-95fd-5496ebd32650", "5efd7a47-98f7-4c4d-87e9-ba7434c4afa6"]'
  @PostMapping("/springdata/teams/{teamId}/players")
  public List<Person> addPlayers(@PathVariable("teamId") Team team, @RequestBody List<UUID> playerIds) {
    return teamService.addPlayers(team, playerIds);
  }

  // TODO: Should we validate that the player exists?
  // If it isn't reference-equal, does it become reference-equal by adding @Transaction to this method?
  // Delete person 4 from team 1:
  // curl -s -X DELETE -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players/5efd7a47-98f7-4c4d-87e9-ba7434c4afa6
  @DeleteMapping("/springdata/teams/{teamId}/players/{playerId}")
  public List<Person> deletePlayer(@PathVariable("teamId") Team team, @PathVariable("playerId") Person player) {
    return teamService.deletePlayer(team, player);
  }

  // TODO: Is there a way to get Spring's argument resolution to populate a List<Person> instead of List<UUID>?
  // Set team 1's players to person 3 and person 4:
  // curl -s -X PUT -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players --data '["9434d65c-a693-4dac-95fd-5496ebd32650", "5efd7a47-98f7-4c4d-87e9-ba7434c4afa6"]'
  @PutMapping("/springdata/teams/{teamId}/players")
  public List<Person> setPlayers(@PathVariable("teamId") Team team, @RequestBody List<UUID> players) {
    return teamService.setPlayers(team, players);
  }

  // TODO: Validate that the team exists
  // Get solved puzzled of Team 1:
  // curl -s -X GET -H 'Content-Type: application/json' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles
  @GetMapping("/springdata/teams/{id}/solvedpuzzles")
  public List<TeamSolvedPuzzle> findSolvedPuzzles(@PathVariable UUID id) {
    return teamRepository.findById(id).get().getTeamSolvedPuzzles();
  }

  // Create a solved relationship between Team 1 and the "lungs" puzzle:
  // TODO: Something a bit weird here... The DomainClassConverter works with @PathVariable and @RequestParam but not @RequestBody, so the expected input is form-encoded because those
  // go into a bucket that Spring can pull with @RequestParam. If we were a bit more serious we may want to investigate whether it's possible to get the DomainClassConverter to work for request
  // bodies where the body is a single ID string.
  // curl -s -X POST -H 'Content-Type: application/x-www-form-urlencoded' -i http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/solvedpuzzles --data puzzle=9e03b12a-dc65-485f-af28-9c5251a5c6f5
  @PostMapping("/springdata/teams/{id}/solvedpuzzles")
  public TeamSolvedPuzzle createSolvedPuzzle(@PathVariable("id") Team team, @RequestParam Puzzle puzzle) {
    if (team == null) throw new ResponseStatusException(NOT_FOUND, "Team not found");
    if (puzzle == null) throw new ResponseStatusException(NOT_FOUND, "Team not found");
    return teamService.createSolvedPuzzle(team, puzzle);
  }

  // TODO: Also validate that the SolvedPuzzle ID is valid/existing in the current team.
  // Update the TeamSolvedPuzzle between Team 1 and the hearts puzzle with some new dates and points values:
  // curl -s -X PUT -H 'Content-Type: application/json' -i http://localhost:8082/springdata/solvedpuzzles/214941d3-98d8-4378-b9f1-c69490e59e26 --data '{"id":"214941d3-98d8-4378-b9f1-c69490e59e26","start":"2015-06-24T09:32:01.001+01:00","end":"2015-06-24T11:52:01.001+01:00","points":"551"}'
  @PutMapping("/springdata/solvedpuzzles/{oldSolvedPuzzleId}")
  public TeamSolvedPuzzle updateSolvedPuzzle(@PathVariable("oldSolvedPuzzleId") TeamSolvedPuzzle oldSolvedPuzzle, @RequestBody TeamSolvedPuzzle newSolvedPuzzle) {
    return teamService.updateSolvedPuzzle(oldSolvedPuzzle, newSolvedPuzzle);
  }

  @DeleteMapping("/springdata/solvedpuzzles/{solvedPuzzleId}")
  // Delete the TeamSolvedPuzzle between Team 1 and the hearts puzzle:
  // curl -s -X DELETE -H 'Content-Type: application/json' -i http://localhost:8082/springdata/solvedpuzzles/214941d3-98d8-4378-b9f1-c69490e59e26
  public void deleteSolvedPuzzle(@PathVariable UUID solvedPuzzleId) {
    teamSolvedPuzzleRepository.deleteById(solvedPuzzleId);
  }
}