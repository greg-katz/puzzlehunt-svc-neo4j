package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Leaderboard;
import com.horshers.puzzlehuntspringdata.repo.HuntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.commons.collections4.IterableUtils.toList;

@RestController("spring-data-hunts-controller")
public class HuntsController {

  @Autowired
  private HuntRepository huntRepository;

  @GetMapping("/springdata/hunts")
  public List<Hunt> getAllHunts() {
    return toList(huntRepository.findAll());
  }

//  @GetMapping("/springdata/hunts/{nameOrUuid}")
//  public Hunt getHunt(@PathVariable String nameOrUuid) {
//    try {
//      return huntRepository.findById(UUID.fromString(nameOrUuid)).get();
//    }
//    catch (IllegalArgumentException iae) {
//      return huntRepository.findByName(nameOrUuid);
//    }
//  }

  @GetMapping("/springdata/hunts/{id}")
  public Hunt getHunt(@PathVariable("id") Hunt hunt) {
    return hunt;
  }

  @GetMapping("/springdata/hunts/requestparamtest")
  public Hunt getHuntRequestParam(Hunt hunt) {
    return hunt;
  }

  @PostMapping("/springdata/hunts")
  public Hunt createHunt(Hunt hunt) {
    return huntRepository.save(hunt);
  }

  @PutMapping("/springdata/hunts/{id}")
  public Hunt updateHunt(@PathVariable("id") Hunt hunt) {
    return huntRepository.save(hunt);
  }

  @GetMapping("/springdata/hunts/{huntName}/leaderboard")
  public Leaderboard getLeaderboard(@PathVariable String huntName) {
    return huntRepository.getLeaderboardByHuntName(huntName);
  }
}