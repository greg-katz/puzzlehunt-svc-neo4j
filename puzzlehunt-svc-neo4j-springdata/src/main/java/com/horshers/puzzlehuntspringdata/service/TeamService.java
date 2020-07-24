package com.horshers.puzzlehuntspringdata.service;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.repo.HuntRepository;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.IterableUtils.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class TeamService {

  @Autowired HuntRepository huntRepository;
  @Autowired PersonRepository personRepository;
  @Autowired TeamRepository teamRepository;

  @Transactional
  public Team createTeam(Team team, UUID huntId) {
    Hunt hunt = huntRepository.findById(huntId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    team = teamRepository.save(team);
    hunt.addTeam(team);
    hunt = huntRepository.save(hunt);
    return hunt.findTeam(team).orElseThrow(() -> new RuntimeException("Should never happen!"));
  }

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
