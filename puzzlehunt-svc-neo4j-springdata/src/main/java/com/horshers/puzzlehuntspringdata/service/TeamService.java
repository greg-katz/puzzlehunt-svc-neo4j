package com.horshers.puzzlehuntspringdata.service;

import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.IterableUtils.toList;

@Component
public class TeamService {

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  PersonRepository personRepository;

  @Transactional
  public List<Person> addPlayers(UUID teamId, List<UUID> playerIds) {
    Team team = teamRepository.findById(teamId).get();
    List<Person> players = toList(personRepository.findAllById(playerIds));
    team.getPlayers().addAll(players);

    return teamRepository.save(team).getPlayers();
  }

  @Transactional
  public List<Person> deletePlayer(UUID teamId, UUID playerId) {
    Team team = teamRepository.findById(teamId).get();
    team.setPlayers(team.getPlayers().stream().filter(player -> !player.getUuid().equals(playerId)).collect(Collectors.toList()));
    return teamRepository.save(team).getPlayers();
  }

  @Transactional
  public List<Person> setPlayers(UUID teamId, List<UUID> playerIds) {
    Team team = teamRepository.findById(teamId).get();
    Iterable<Person> players = personRepository.findAllById(playerIds);

    team.setPlayers(toList(players));

    return teamRepository.save(team).getPlayers();

  }
}
