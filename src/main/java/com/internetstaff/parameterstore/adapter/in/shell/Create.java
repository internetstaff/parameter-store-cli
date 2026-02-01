package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ssm.model.ParameterType;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class Create {
  private final ParameterStore parameterStore;
  private final Terminal terminal;
  private final OptionPrompter promptingService;

  @Command(description = "Create Parameter", group = "Parameter Store")
  public void create(
      @Option(description = "Parameter Type") String type,
      @Argument(index = 0, description = "Parameter name") String name,
      @Argument(index = 1, description = "Parameter value") String value
  ) {
    var paramName = Optional.ofNullable(name)
        .map(StringUtils::trimToNull)
        .orElseGet(() -> promptingService.prompt("Parameter name: "));

    var paramValue = Optional.ofNullable(value)
        .map(StringUtils::trimToNull)
        .orElseGet(() -> promptingService.prompt("Parameter value: "));

    var paramType = Optional.ofNullable(type)
        .map(StringUtils::trimToNull)
        .orElseGet(() -> promptingService.select("Parameter type: ",
            ParameterType.knownValues().stream()
                .collect(Collectors.toMap(ParameterType::toString, ParameterType::toString))));

    terminal.writer().println("Creating type: %s name: %s value:%s type:%s".formatted(paramType, paramName, paramValue, paramType));

    parameterStore.createParameter(paramName, paramValue, paramType);
  }

}
