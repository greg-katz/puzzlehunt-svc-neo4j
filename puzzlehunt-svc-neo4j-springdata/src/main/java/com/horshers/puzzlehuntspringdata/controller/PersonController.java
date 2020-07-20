package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import com.horshers.puzzlehuntspringdata.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController("spring-data-person-controller")
public class PersonController {

  @Autowired
  PersonService personService;

  @Autowired
  PersonRepository personRepository;

  @RequestMapping("/springdata/person")
  public Iterable<Person> findAll() {
    return personRepository.findAll();
  }

  @RequestMapping("/springdata/crazystupidupdate")
  public Person save() {
    return personService.crazyStupidTeamSwap();
  }

  @RequestMapping("/springdata/person/{ID}")
  public Optional<Person> findById(@PathVariable("ID") UUID id, @RequestParam(defaultValue = "1") int depth) {
    return personRepository.findById(id, depth);
  }

  @RequestMapping(value="/springdata/person", params="name")
  public Person findByName(@RequestParam String name) {
    return personRepository.findByName(name);
  }
}
