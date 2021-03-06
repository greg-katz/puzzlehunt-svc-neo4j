package com.horshers.puzzlehuntdriver.controller;

import com.horshers.puzzlehuntdriver.dao.LeaderboardDao;
import com.horshers.puzzlehuntdriver.model.Leaderboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {

  @Autowired
  LeaderboardDao puzzleHuntDao;

  @RequestMapping("/driver/leaderboard")
  public Leaderboard leaderboard() {
    Leaderboard leaderboard = puzzleHuntDao.getLeaderboard();
    return leaderboard;
  }
}
