package com.horshers.puzzlehunt.driver.controller;

import com.horshers.puzzlehunt.driver.dao.PlayerDao;
import com.horshers.puzzlehunt.driver.model.Player;
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

  @RequestMapping("/driver/player/{uuid}")
  public Player readPlayer(@PathVariable("uuid") UUID playerId) {
    return playerDao.readPlayer(playerId);
  }

  @RequestMapping("/driver/player/{uuid}/update")
  public Player updatePlayer(@PathVariable("uuid") UUID playerId,
                             @RequestParam String name) {
    return playerDao.updatePlayer(playerId, name);
  }

  @RequestMapping("/driver/player/{uuid}/remove")
  public boolean removePlayer(@PathVariable("uuid") UUID playerId) {
    return playerDao.removePlayer(playerId);
  }

  @RequestMapping("/driver/player/{uuid}/changeteam")
  public Player changeTeam(@PathVariable("uuid") UUID playerId,
                          @RequestParam UUID newTeamId) {
    return playerDao.changeTeam(playerId, newTeamId);
  }
}
