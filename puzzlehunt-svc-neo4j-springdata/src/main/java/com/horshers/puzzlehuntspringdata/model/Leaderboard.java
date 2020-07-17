package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;

import java.util.List;

@Data
public class Leaderboard {
  List<TeamResult> teamResults;
}