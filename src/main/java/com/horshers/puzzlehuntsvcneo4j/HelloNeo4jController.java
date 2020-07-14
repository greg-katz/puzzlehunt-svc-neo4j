package com.horshers.puzzlehuntsvcneo4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloNeo4jController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello, Neo4j!";
  }
}
