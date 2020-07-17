package com.horshers.puzzlehuntdriver.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {
  private UUID id;
  private String name;
  private Team team;
}