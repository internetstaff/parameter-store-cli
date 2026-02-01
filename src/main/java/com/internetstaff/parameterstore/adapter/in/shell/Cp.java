package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class Cp {
  private final ParameterStore parameterStore;

  @Command(description = "Copy Parameter", group = "Parameter Store")
  public String cp(
      @Argument(index = 0, description = "Full path of source parameter") String source,
      @Argument(index = 1, description = "Full path of destination parameter") String destination
  ) {

    if (parameterStore.copyParameter(source, destination)) {
      return "%s copied to %s.".formatted(source, destination);
    } else {
      return "Parameter not found";
    }
  }
}
