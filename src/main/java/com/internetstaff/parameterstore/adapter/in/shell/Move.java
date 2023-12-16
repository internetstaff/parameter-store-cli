package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
class Move {
  private final ParameterStore parameterStore;

  @Command(description = "Move Parameter", group = "Parameter Store")
  public String mv(
      @Option(description = "Full path of source parameter") String source,
      @Option(description = "Full path of destination parameter") String destination
  ) {

    if (parameterStore.copyParameter(source, destination)) {

      if (parameterStore.deleteParameter(source)) {
        return "%s moved to %s.".formatted(source, destination);
      } else {
        return "%s created, but %s was unable to be removed.".formatted(destination, source);
      }

    } else {
      return "Parameter not found";
    }
  }
}
