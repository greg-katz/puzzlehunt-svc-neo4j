package com.horshers.puzzlehuntdriver.model;

import lombok.Data;

@Data
public class Hint {
  private String text;
  private int unlockMins;
  private int cost;
  private Puzzle puzzle;
}