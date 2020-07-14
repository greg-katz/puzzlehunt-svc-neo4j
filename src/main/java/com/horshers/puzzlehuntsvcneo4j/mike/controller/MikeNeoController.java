package com.horshers.puzzlehuntsvcneo4j.mike.controller;

import com.horshers.puzzlehuntsvcneo4j.mike.dao.LeaderboardDao;
import com.horshers.puzzlehuntsvcneo4j.model.Leaderboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MikeNeoController {

  @Autowired
  LeaderboardDao puzzleHuntDao;

  @RequestMapping("/mike/leaderboard")
  public Leaderboard leaderboard() {
    Leaderboard leaderboard = puzzleHuntDao.getLeaderboard();
    return leaderboard;
  }
}
