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
