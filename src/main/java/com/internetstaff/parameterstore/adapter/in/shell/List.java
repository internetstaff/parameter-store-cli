package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import com.internetstaff.parameterstore.application.port.out.ParameterStore.Metadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.Comparator;

@Command
@RequiredArgsConstructor
class List {
  private final ParameterStore parameterStore;

  private int maxLength(java.util.List<Metadata> parameters) {
    return parameters.stream()
        .map(Metadata::name)
        .map(String::length)
        .max(Comparator.naturalOrder())
        .orElse(0);
  }

  @Command(description = "List parameters", group = "Parameter Store")
  public String ls(
      @Option(defaultValue = "*", description = "Glob pattern") String path
  ) {

    var parameters = parameterStore.getParameters(path);
    var len = maxLength(parameters);

    var result = new StringBuilder();

    for (var parameter : parameters) {
      result.append(StringUtils.rightPad(parameter.name(), len))
          .append(" ")
          .append(parameter.lastModifiedDate())
          .append("\n");
    }

    result.append("%s parameters found.".formatted(parameters.size()));

    return result.toString();
  }
}
