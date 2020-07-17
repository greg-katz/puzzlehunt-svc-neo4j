package com.horshers.puzzlehuntdriver.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Team {
  private UUID id;
  private String name;
  private Player captain;
  private List<Player> players;
  // TODO: Probably Team should have a hunt field, right?
  //private Hunt hunt;
}