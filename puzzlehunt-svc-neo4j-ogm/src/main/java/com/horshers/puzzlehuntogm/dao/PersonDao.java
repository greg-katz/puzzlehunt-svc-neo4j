package com.horshers.puzzlehuntogm.dao;

import com.horshers.puzzlehuntogm.model.Person;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonDao {

  @Autowired
  @Qualifier("ogm-session-factory")
  SessionFactory sessionFactory;

  public List<Person> findAll() {
    Session session = sessionFactory.openSession();

    return new ArrayList(session.loadAll(Person.class, 1));
  }
}
