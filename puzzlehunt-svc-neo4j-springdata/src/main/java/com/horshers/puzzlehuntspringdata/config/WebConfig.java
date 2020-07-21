package com.horshers.puzzlehuntspringdata.config;

import com.horshers.puzzlehuntspringdata.model.Hunt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.format.DistanceFormatter;
import org.springframework.data.geo.format.PointFormatter;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/*@EnableWebMvc
@EnableSpringDataWebSupport*/
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private ApplicationContext context;

  // TODO: This bean doesn't need to exist - DELETE
  @Bean
  public Hunt bogusHunt() {
    return new Hunt();
  }

  /**
   * This is a workaround to https://jira.spring.io/browse/DATACMNS-1759
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    if (!(registry instanceof FormattingConversionService)) {
      return;
    }

    FormattingConversionService conversionService = (FormattingConversionService) registry;

    DomainClassConverter<FormattingConversionService> converter = new DomainClassConverter<>(conversionService);
    converter.setApplicationContext(context);
    registry.addConverter(converter);
  }
}