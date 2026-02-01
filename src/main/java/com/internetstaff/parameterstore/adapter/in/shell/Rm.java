package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class Rm {
  private final ParameterStore parameterStore;
  private final OptionPrompter promptingService;

  @Command(description = "Remove parameter", group = "Parameter Store")
  public String rm(
      @Option(description = "Force without prompting", shortName = 'f', defaultValue = "false") boolean force,
      @Argument(index = 0, description = "Full path of parameter to remove") String name
  ) {

    if (force || promptingService.confirm("Are you sure you want to remove parameter '%s'?".formatted(name))) {

      if (parameterStore.deleteParameter(name)) {
        return "%s removed.".formatted(name);
      } else {
        return "Parameter not found";
      }
    } else {
      return "Operation cancelled";
    }
  }
}