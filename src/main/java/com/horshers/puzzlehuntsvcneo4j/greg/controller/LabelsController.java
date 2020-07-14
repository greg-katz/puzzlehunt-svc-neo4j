package com.horshers.puzzlehuntsvcneo4j.greg.controller;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LabelsController {

  @GetMapping("/greg/labels")
  public List<String> labels() {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
    Session session = driver.session();
    Result result = session.run("match (n) return n");
    // TODO: labels() returns an Iterable<String>. How do you gracefully get the 0th label?
    return result.list(r -> r.get("n").asNode().labels().toString());
  }
}
