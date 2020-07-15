package com.horshers.puzzlehunt.driver.controller;

import com.horshers.puzzlehunt.driver.dao.TeamDao;
import com.horshers.puzzlehunt.driver.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}