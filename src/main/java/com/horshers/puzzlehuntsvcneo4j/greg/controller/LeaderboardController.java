package com.horshers.puzzlehuntsvcneo4j.greg.controller;

import com.horshers.puzzlehuntsvcneo4j.model.Leaderboard;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {

  private static String query =
    "TODO";

  @GetMapping("/leaderboard")
  public Leaderboard leaderboard() {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));

    Leaderboard leaderboard = new Leaderboard();

    try (Session session = driver.session()) {
      Result result = session.run(query);
      // TODO: Add TeamResult[] to leaderboard
    }

    return leaderboard;
  }
}
