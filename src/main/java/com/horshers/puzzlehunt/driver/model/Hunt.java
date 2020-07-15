package com.horshers.puzzlehunt.driver.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Hunt {
  private UUID id;
  private String name;
  private Instant start;
  private Instant end;
  private List<Team> teams;
}
