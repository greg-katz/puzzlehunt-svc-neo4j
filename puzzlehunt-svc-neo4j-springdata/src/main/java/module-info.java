open module puzzlehunt.svc.neo4j.springdata {
  requires spring.context;
  requires spring.data.neo4j;
  requires org.neo4j.ogm.core;
  requires org.neo4j.driver;
  requires org.neo4j.ogm.drivers.api;
  requires spring.tx;
  requires spring.core;
  requires spring.web;
  requires spring.beans;
  requires spring.data.commons;
  requires spring.webmvc;
  requires lombok;
  requires com.fasterxml.jackson.annotation;
  requires org.apache.commons.collections4;
  requires org.apache.tomcat.embed.core;
  requires spring.boot;
}