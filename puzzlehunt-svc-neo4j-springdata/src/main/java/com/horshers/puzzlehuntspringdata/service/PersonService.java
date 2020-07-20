package com.horshers.puzzlehuntspringdata.service;

import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.model.Team;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  PersonRepository personRepository;

  // TODO: This shouldn't be here.
  @Transactional
  public Person crazyStupidTeamSwap() {
    Team team1 = teamRepository.findById(UUID.fromString("66af7be3-7240-48a9-9e19-f2ff0e885910")).get();
    Team team2 = teamRepository.findById(UUID.fromString("4090c00c-1219-4cf3-877e-146f345ec498")).get();

    Person person = personRepository.findById(UUID.fromString("5efd7a47-98f7-4c4d-87e9-ba7434c4afa6"), 0).get();

    if (team1.getPlayers().contains(person)) {
      team1.getPlayers().remove(person);
      team2.getPlayers().add(person);
    }
    else {
      team2.getPlayers().remove(person);
      team1.getPlayers().add(person);
    }

    teamRepository.save(team1);
    teamRepository.save(team2);
    return null;
  }
}
