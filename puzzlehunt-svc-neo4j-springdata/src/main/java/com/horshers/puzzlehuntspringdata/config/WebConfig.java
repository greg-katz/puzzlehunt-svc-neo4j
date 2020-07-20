package com.horshers.puzzlehuntspringdata.config;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
public class WebConfig {

  // TODO: This bean doesn't need to exist - DELETE
  @Bean
  public Hunt bogusHunt() {
    return new Hunt();
  }
}