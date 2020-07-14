package com.horshers.puzzlehuntsvcneo4j.dao;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PuzzleHuntDao {

  public Object literallyAnyData() {
    Map anyData = new HashMap<>();
    anyData.put("data", "test");
    return anyData;
  }
}
