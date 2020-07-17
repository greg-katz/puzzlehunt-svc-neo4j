package com.horshers.puzzlehuntspringdata.controller;

import com.horshers.puzzlehuntspringdata.model.Person;
import com.horshers.puzzlehuntspringdata.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("spring-data-person-controller")
public class PersonController {

  @Autowired
  PersonRepository personRepository;

  @RequestMapping("/springdata/person")
  public Iterable<Person> findAll() {
    return personRepository.findAll();
  }

  @RequestMapping(value="/springdata/person", params="name")
  public Person findByName(@RequestParam String name) {
    return personRepository.findByName(name);
  }
}
