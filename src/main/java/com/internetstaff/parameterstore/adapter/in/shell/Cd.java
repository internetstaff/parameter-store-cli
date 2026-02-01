package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.in.SetCurrentDirectoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Cd {
  private final SetCurrentDirectoryUseCase setCurrentDirectoryUseCase;

  @Command(description = "Change directory", group = "Parameter Store")
  public void cd(
      @Argument(index = 0, description = "Path") String path
  ) {
    setCurrentDirectoryUseCase.setCurrentDirectory(path);
  }

}
