open module puzzlehunt.svc.neo4j.springdata {
  requires spring.context;
  requires spring.data.neo4j;
  requires org.neo4j.ogm.core;
  requires org.neo4j.ogm.drivers.api;
  requires spring.web;
  requires spring.beans;
  requires lombok;
  requires com.fasterxml.jackson.annotation;
  exports com.horshers.puzzlehuntspringdata.controller;
  exports com.horshers.puzzlehuntspringdata.repo;
}