package com.horshers.puzzlehunt.ogm.dao;

import com.horshers.puzzlehunt.ogm.model.Person;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PersonDao {

  @Autowired
  SessionFactory sessionFactory;

  public List<Person> findAll() {
    Session session = sessionFactory.openSession();

    Collection<Person> collection = session.loadAll(Person.class, 0);

    return new ArrayList<>(collection);
  }
}
