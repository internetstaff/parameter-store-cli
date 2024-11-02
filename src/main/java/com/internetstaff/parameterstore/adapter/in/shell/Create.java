package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.AbstractShellComponent;
import software.amazon.awssdk.services.ssm.model.ParameterType;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command
class Create extends AbstractShellComponent {
  private final ParameterStore parameterStore;
  private final Terminal terminal;
  private final OptionPrompter promptingService;

  @Command(description = "Create Parameter", group = "Parameter Store")
  public void create(
      @Option ParameterType type,
      @Option String name,
      @Option String value
  ) {
    var paramName = Optional.ofNullable(name)
        .orElseGet(() -> promptingService.prompt("Parameter name: "));

    var paramValue = Optional.ofNullable(value)
        .orElseGet(() -> promptingService.prompt("Parameter value: "));

    var paramType = Optional.ofNullable(type)
        .orElseGet(() -> promptingService.select("Parameter type: ", ParameterType.knownValues().stream()
            .collect(Collectors.toMap(ParameterType::name, Function.identity()))));

    terminal.writer().println("type: %s name: %s value:%s type:%s".formatted(type, paramName, paramValue, paramType));
  }

}
