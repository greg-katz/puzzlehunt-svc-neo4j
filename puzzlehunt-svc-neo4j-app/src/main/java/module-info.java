open module puzzlehunt.svc.neo4j.app {
  requires spring.boot.autoconfigure;
  requires spring.boot;
  requires java.sql;
  requires com.fasterxml.jackson.core;
  requires io.github.classgraph;
  requires spring.context;
  requires puzzlehunt.svc.neo4j.driver;
  requires puzzlehunt.svc.neo4j.ogm;
  requires puzzlehunt.svc.neo4j.springdata;
}