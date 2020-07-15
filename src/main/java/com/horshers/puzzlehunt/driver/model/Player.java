package com.horshers.puzzlehunt.driver.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {
  private UUID id;
  private String name;
  private Team team;
}