package com.horshers.puzzlehuntspringdata.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<Neo4jOgmSessionFilter> neo4jOgmSessionFilterRegistrationBean(){
    FilterRegistrationBean<Neo4jOgmSessionFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(neo4jOgmSessionFilter());
    registrationBean.addUrlPatterns("/springdata/*");

    return registrationBean;
  }

  @Bean
  public Neo4jOgmSessionFilter neo4jOgmSessionFilter() {
    return new Neo4jOgmSessionFilter();
  }

  /*
    Important note: Sessions are OGM's main concept through which you make database queries. OGM sessions
    get mapped to Spring's Transaction abstraction, so for purposes of the following discussion you can think of the
    two words as essentially identical. Got it? Read on...

    OGM sessions are really important. A lot of things can start getting weird if their boundaries aren't where they
    should be.

    For one thing, OGM has caching behavior within a session, so if you look up the same entity via
    different queries within the same transaction there is presumably some performance win there. Read queries being in
    the same transaction also means the Java objects you get back to represent the object from multiple queries will be
    instance-equal, which is convenient and can head off certain kinds of pitfalls like updating an entity without
    realizing there's another instance of the same entity in the object graph you're saving that doesn't have your
    updates.

    But the biggest session pitfall has to do with operations where you're trying to update relationships between
    nodes. Doing this involves a process like this:

    1. Fetch an instance of the entity you want the updated relationship(s) to come from.
    2. Fetch the entity instances you want to create relationships to.
    3. Set fields on the first "from" entity with references to the "to" entities. These may be single object fields or in collections.
    4. Call the save method on the first "from" entity to persist your changes.

    If all of these steps happen inside the same transaction this will work the way you expect. When you save the entity
    the OGM session will figure out the differences the relationships coming from your entity in the database and those
    represented in the object graph you're saving, and it will issue whatever create and delete statements are needed to
    make the database relationships match those in your object graph.

    If you do these statements in separate transactions things get screwy. When you call save OGM is trying to
    diff the object graph it's loaded so far to the one represented by the object you're saving to figure out what
    queries to run. But if you didn't do your fetches inside the same transaction as the save OGM doesn't have any
    object graph it's "loaded so far" to diff against. It will still see the object graph represented by the object
    you're saving and run queries to create the relationships represented in it, but it won't notice any relationships
    you removed and therefore won't issue any deletes. You might do something like update the list of players on a team
    thinking that you're providing the new complete list, but what will actually end up happening when you save is that
    new players will be added to the team, but no old players will be removed.

    Basically a good rule of thumb is that if you're doing an operation that involves multiple queries it's probably
    wise to wrap them in the same transaction to avoid unexpected kooky behavior.

    Enter the Neo4jOgmSessionFilter, which essentially creates a "request-scoped" transaction that all of the
    controllers at its mapped endpoints can assume already exists. This should prevent the endpoint authors from
    having to worry about transaction management - they can just assume that whatever queries they provoke are happening
    in the same one.

    This is extra convenient with the DomainClassConverter, which is the thing that allows you to specify a controller
    parameter like this and have it automatically resolve to the appropriate entity object by calling the appropriate
    repository's findById method:

    @PathVariable("teamId") Team team

    Without a request-scoped transaction being created before controller method resolution happens any use of this
    kind of parameter resolution would happen outside any transaction that the endpoint author can create, meaning
    they're set up for confusing and buggy behavior with any use of this feature in a controller that isn't trivially
    simple. The Neo4jOgmSessionFilter disarms the danger of this feature so we can use it to make controllers look
    better without stepping on any landmines.
   */
  public static class Neo4jOgmSessionFilter implements Filter {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
      transactionTemplate.execute(status -> {
        try {
          chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
          throw new RuntimeException(e);
        }
        return null;
      });
    }
  }
}
