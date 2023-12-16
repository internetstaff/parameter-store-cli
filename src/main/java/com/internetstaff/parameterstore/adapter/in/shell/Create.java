package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.AbstractShellComponent;

@RequiredArgsConstructor
@Command
class Create extends AbstractShellComponent {
  private final ParameterStore parameterStore;

  @Command(description = "Create Parameter", group = "Parameter Store")
  public void create(
      @Option(longNames = "type") String type,
      @Option String value,
      @Option String name
  ) {
    getTerminal().writer().println("type: %s name: %s  value:%s".formatted(type, name, value));
  }
}
