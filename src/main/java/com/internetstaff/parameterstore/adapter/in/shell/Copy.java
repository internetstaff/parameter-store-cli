package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
class Copy {
  private final ParameterStore parameterStore;

  @Command(description = "Copy Parameter", group = "Parameter Store")
  public String cp(
      @Option(description = "Full path of source parameter") String source,
      @Option(description = "Full path of destination parameter") String destination
  ) {

    if (parameterStore.copyParameter(source, destination)) {
      return "%s copied to %s.".formatted(source, destination);
    } else {
      return "Parameter not found";
    }
  }
}
