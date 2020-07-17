package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections4.IterableUtils.toList;

@RestController("spring-data-teams-controller")
public class TeamsController {

  @Autowired
  private TeamRepository teamRepository;

  @GetMapping("/springdata/hunts/{hunt}/teams")
  public List<Team> teams(@PathVariable UUID hunt) {
    // TODO: How do you look up the teams for the hunt? Do you need to look up the Hunt object first so you can pass
    //  it in? Can the repository define a query with @Query that takes a hunt UUID?
    return toList(teamRepository.findAll());
  }

  @GetMapping("/springdata/hunts/{hunt}/teams/{nameOrUuid}")
  public Team team(@PathVariable UUID hunt, @PathVariable String nameOrUuid) {
    // TODO: Use the hunt parameter
    try {
      return teamRepository.findById(UUID.fromString(nameOrUuid)).get();
    }
    catch (IllegalArgumentException iae) {
      return teamRepository.findByName(nameOrUuid);
    }
  }
}