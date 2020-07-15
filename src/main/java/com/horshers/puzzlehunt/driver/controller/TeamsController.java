package com.horshers.puzzlehunt.driver.controller;

import com.horshers.puzzlehunt.driver.dao.TeamDao;
import com.horshers.puzzlehunt.driver.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class TeamsController {

  @Autowired
  private TeamDao teamDao;

  @GetMapping("/driver/hunts/{hunt}/teams")
  public List<Team> teams(@PathVariable UUID hunt) {
    return teamDao.getTeams(hunt);
  }

  @GetMapping("/driver/hunts/{hunt}/teams/{team}")
  public Team team(@PathVariable UUID hunt, @PathVariable UUID team) {
    return teamDao.getTeam(hunt, team);
  }

  // TODO: This should be a @PostMapping to .../teams, not a @GetMapping to .../teams/create
  // TODO: Add support for including references to existing captain and player nodes when creating a team
  // TODO: Add support for creating new captain and player nodes when creating a team
  @GetMapping("/driver/hunts/{hunt}/teams/create")
  public Team createTeam(@PathVariable UUID hunt, @RequestParam String name) {
    return teamDao.createTeam(hunt, name);
  }
}