package com.horshers.puzzlehuntdriver.controller;

import com.horshers.puzzlehuntdriver.dao.PlayerDao;
import com.horshers.puzzlehuntdriver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PlayerController {

  @Autowired
  PlayerDao playerDao;

  @RequestMapping("/driver/player/new/{teamId}")
  public Player createPlayer(@PathVariable("teamId") UUID teamId, @RequestParam String name) {
    return playerDao.createPlayer(teamId, name);
  }

  @RequestMapping("/driver/player/{id}")
  public Player readPlayer(@PathVariable("id") UUID playerId) {
    return playerDao.readPlayer(playerId);
  }

  @RequestMapping("/driver/player/{id}/update")
  public Player updatePlayer(@PathVariable("id") UUID playerId,
                             @RequestParam String name) {
    return playerDao.updatePlayer(playerId, name);
  }

  @RequestMapping("/driver/player/{id}/remove")
  public boolean removePlayer(@PathVariable("id") UUID playerId) {
    return playerDao.removePlayer(playerId);
  }

  @RequestMapping("/driver/player/{id}/changeteam")
  public Player changeTeam(@PathVariable("id") UUID playerId,
                          @RequestParam UUID newTeamId) {
    return playerDao.changeTeam(playerId, newTeamId);
  }
}
