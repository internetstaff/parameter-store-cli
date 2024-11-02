package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.in.SetCurrentDirectoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command
@RequiredArgsConstructor
public class Cd {
  private final SetCurrentDirectoryUseCase setCurrentDirectoryUseCase;

  @Command(description = "Change directory", group = "Parameter Store")
  public void Cd(
      @Option(description = "Path") String path
  ) {
    setCurrentDirectoryUseCase.setCurrentDirectory(path);
  }

}
