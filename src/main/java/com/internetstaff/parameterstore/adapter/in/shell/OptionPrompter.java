package com.internetstaff.parameterstore.adapter.in.shell;

import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.jline.tui.component.SingleItemSelector;
import org.springframework.shell.jline.tui.component.StringInput;
import org.springframework.shell.jline.tui.component.support.SelectorItem;
import org.springframework.shell.jline.tui.style.TemplateExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
class OptionPrompter {
  private final Terminal terminal;
  private final ResourceLoader resourceLoader;
  private final TemplateExecutor templateExecutor;

  public String prompt(String name) {
    var component = new StringInput(terminal, name, null);
    component.setResourceLoader(resourceLoader);
    component.setTemplateExecutor(templateExecutor);

    return component.run(StringInput.StringInputContext.empty())
        .getResultValue();
  }

  public <T> T select(String name, Map<String, T> options) {
    var items = options.entrySet().stream()
        .map(option -> SelectorItem.of(option.getKey(), option.getValue()))
        .toList();

    var component = new SingleItemSelector<>(terminal, items, name, null);
    component.setResourceLoader(resourceLoader);
    component.setTemplateExecutor(templateExecutor);

    return component
        .run(SingleItemSelector.SingleItemSelectorContext.empty())
        .getResultItem()
        .map(SelectorItem::getItem)
        .orElse(null);
  }

  public boolean confirm(String message) {
    terminal.writer().print("%s [y/N] ".formatted(message));
    terminal.writer().flush();

    try {
      int ch = terminal.reader().read();
      terminal.writer().println();
      terminal.writer().flush();
      return ch == 'y' || ch == 'Y';
    } catch (IOException e) {
      return false;
    }
  }

}
