package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
class Rm {
  private final ParameterStore parameterStore;

  @Command(description = "Remove parameter", group = "Parameter Store")
  public String rm(
      @Option(description = "Full path of parameter to remove", required = true) String name
  ) {

    if (parameterStore.deleteParameter(name)) {
      return "%s removed.".formatted(name);
    } else {
      return "Parameter not found";
    }
  }
}