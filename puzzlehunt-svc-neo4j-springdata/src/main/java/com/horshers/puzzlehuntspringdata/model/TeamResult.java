package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.AttributeConverter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.Duration;

@Data
@QueryResult
public class TeamResult {

  private String name;
  private boolean finished;
  private int score;

  @Convert(IsoDurationConverter.class)
  private String time;

  public static class IsoDurationConverter implements AttributeConverter<String, IsoDuration> {

    @Override
    public IsoDuration toGraphProperty(String value) {
      return null;
    }

    @Override
    public String toEntityAttribute(IsoDuration value) {
      Duration time = Duration.ofSeconds(value.seconds(), value.nanoseconds());
      return String.format("%d:%02d:%02d", time.toHoursPart(), time.toMinutesPart(), time.toSecondsPart());
    }
  }
}