package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.AbstractShellComponent;

@Command
@RequiredArgsConstructor
class Remove extends AbstractShellComponent {
  private final ParameterStore parameterStore;
  @Autowired
  @Lazy // Avoid circular reference
  private LineReader lineReader; // StringInput components don't work in Intellij Terminal

  @Command(description = "Remove parameter", group = "Parameter Store")
  // Not a clue how to get Spring Shell to support -f without an argument, yet not try to use `name` as the
  // argument to -f.  Arity doesn't help.
  public String rm(
      @Option(description = "Force without prompting", shortNames = 'f', defaultValue = "false") boolean force,
      @Option(description = "Full path of parameter to remove", required = true) String name
  ) {

    if (!force) {
      //noinspection resource
      getTerminal().writer().println("This command will permanently delete parameter %s.".formatted(name));

      var value = this.lineReader.readLine("Type yes to confirm:");

      if (!"yes".equals(value)) {
        return "Remove aborted.";
      }
    }

    if (parameterStore.deleteParameter(name)) {
      return "%s removed.".formatted(name);
    } else {
      return "Parameter not found";
    }
  }
}