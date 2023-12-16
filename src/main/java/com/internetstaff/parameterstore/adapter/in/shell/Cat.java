package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
class Cat {
  private final ParameterStore parameterStore;

  @Command(description = "Cat Parameter", group = "Parameter Store")
  public String cat(
      @Option(description = "Full path of parameter") String name
  ) {

    var result = parameterStore.getParameter(name);

    if (result.isEmpty()) {
      return "Parameter not found";
    } else {
      var parameter = result.get();
      return
          """
              Name=%s
              Type=%s
              Value=%s
              LastModifiedDate=%s
              """.formatted(
              parameter.name(),
              parameter.type(),
              parameter.value(),
              parameter.lastModifiedDate()
          );

    }
  }

}
