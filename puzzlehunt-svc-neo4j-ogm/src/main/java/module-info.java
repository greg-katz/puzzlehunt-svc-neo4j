open module puzzlehunt.svc.neo4j.ogm {
  requires spring.web;
  requires spring.context;
  requires spring.beans;
  requires org.neo4j.ogm.core;
  requires lombok;
  requires com.fasterxml.jackson.annotation;
  requires org.neo4j.ogm.drivers.api;
}