package com.internetstaff.parameterstore.application;

import com.internetstaff.parameterstore.application.port.in.GetCurrentDirectoryUseCase;
import com.internetstaff.parameterstore.application.port.in.SetCurrentDirectoryUseCase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@AllArgsConstructor
@NoArgsConstructor
class CurrentDirectoryService implements GetCurrentDirectoryUseCase, SetCurrentDirectoryUseCase {
  private static final String ROOT_DIRECTORY = "/";

  private String currentDirectory = ROOT_DIRECTORY;

  @Override
  public String getCurrentDirectory() {
    return currentDirectory;
  }

  @Override
  public void setCurrentDirectory(String newDirectory) {
    if (StringUtils.isBlank(newDirectory)) {
      currentDirectory = ROOT_DIRECTORY;
    } else if (StringUtils.startsWith(newDirectory, "/")) {
      currentDirectory = Path.of(newDirectory).normalize().toString();
    } else {
      currentDirectory = Path.of(this.currentDirectory + "/" + newDirectory).normalize().toString();
    }
  }
}
