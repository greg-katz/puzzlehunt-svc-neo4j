package com.horshers.puzzlehuntogm.controller;

import com.horshers.puzzlehuntogm.dao.PersonDao;
import com.horshers.puzzlehuntogm.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("ogm-person-controller")
public class PersonController {

  @Autowired
  PersonDao personDao;

  @RequestMapping("/ogm/person")
  public List<Person> readAllPersons() {
    return personDao.findAll();
  }
}
