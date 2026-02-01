package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class Mv {
  private final ParameterStore parameterStore;

  @Command(description = "Move Parameter", group = "Parameter Store")
  public String mv(
      @Argument(index = 0, description = "Full path of source parameter") String source,
      @Argument(index = 1, description = "Full path of destination parameter") String destination
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
