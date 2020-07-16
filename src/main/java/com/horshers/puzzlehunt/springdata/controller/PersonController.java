package com.horshers.puzzlehunt.springdata.controller;

import com.horshers.puzzlehunt.springdata.model.Person;
import com.horshers.puzzlehunt.springdata.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("spring-data-person-controller")
public class PersonController {

  @Autowired
  PersonRepository personRepository;

  @RequestMapping("/springdata/person")
  public Iterable<Person> findAll() {
    return personRepository.findAll();
  }
}
