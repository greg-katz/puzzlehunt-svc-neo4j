package com.horshers.puzzlehunt.driver.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Puzzle {
  private UUID id;
  private String name;
  private List<Hint> hints;
}