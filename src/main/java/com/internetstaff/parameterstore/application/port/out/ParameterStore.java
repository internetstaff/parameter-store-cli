package com.internetstaff.parameterstore.application.port.out;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ParameterStore {

  Optional<Parameter> getParameter(String name);

  List<Metadata> getParameters(String glob);

  boolean copyParameter(String source, String destination);

  boolean deleteParameter(String name);

  @Builder
  record Metadata(
      String name,
      Instant lastModifiedDate
  ) {
  }

  @Builder
  record Parameter(
      String name,
      String type,
      String value,
      Instant lastModifiedDate
  ) {
  }
}
