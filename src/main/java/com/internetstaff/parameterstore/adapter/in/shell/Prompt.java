package com.internetstaff.parameterstore.adapter.in.shell;

import com.internetstaff.parameterstore.application.port.in.GetCurrentDirectoryUseCase;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class Prompt implements PromptProvider {
  private final GetCurrentDirectoryUseCase getCurrentDirectoryUseCase;

  @Override
  public AttributedString getPrompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
        .append("param store")
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE))
        .append(": ")
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
        .append(getCurrentDirectoryUseCase.getCurrentDirectory())
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE))
        .append("$ ")
        .toAttributedString();
  }
}
