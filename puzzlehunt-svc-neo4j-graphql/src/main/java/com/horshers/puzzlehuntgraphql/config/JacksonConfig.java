package com.horshers.puzzlehuntgraphql.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.neo4j.driver.types.IsoDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;

import static java.lang.String.format;

@Configuration
public class JacksonConfig {

  @Autowired
  ObjectMapper objectMapper;

  @PostConstruct
  void initObjectMapper() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(IsoDuration.class, new IsoDurationSerializer());
    objectMapper.registerModule(module);
  }

  /**
   * The neo4j non-OGM driver will return IsoDuration objects for its duration database type. IsoDuration isn't
   * naturally friendly to jackson serialization so this custom serializer will make sure it gets a good-looking format.
   *
   * Arguably cypher queries should be formatting these themselves - doing it here is inflexible if different queries
   * wanted different formats. Still, it is perhaps not a bad thing for us to have a reasonable default format.
   */
  static class IsoDurationSerializer extends StdSerializer<IsoDuration> {

    public IsoDurationSerializer() {
      this(null);
    }

    public IsoDurationSerializer(Class<IsoDuration> t) {
      super(t);
    }

    @Override
    public void serialize(IsoDuration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      Duration time = Duration.ofSeconds(value.seconds(), value.nanoseconds());
      gen.writeString(format("%d:%02d:%02d", time.toHoursPart(), time.toMinutesPart(), time.toSecondsPart()));
    }
  }
}
