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
  private static final String DIRECTORY_SEPARATOR = "/";

  private String currentDirectory = DIRECTORY_SEPARATOR;

  @Override
  public String getCurrentDirectory() {
    return currentDirectory;
  }

  @Override
  public void setCurrentDirectory(String newDirectory) {
    if (StringUtils.isBlank(newDirectory)) {
      currentDirectory = DIRECTORY_SEPARATOR;
    } else if (StringUtils.startsWith(newDirectory, DIRECTORY_SEPARATOR)) {
      currentDirectory = normalize(newDirectory);
    } else {
      currentDirectory = normalize(this.currentDirectory + DIRECTORY_SEPARATOR + newDirectory);
    }
  }

  private String normalize(String path) {
    return Path.of(path).normalize().toString();
  }

  @Override
  public String qualifyName(String name) {
    if (StringUtils.startsWith(name, "/")) {
      return name;
    } else {
      return normalize(currentDirectory + DIRECTORY_SEPARATOR + name);
    }
  }

  @Override
  public String baseName(String name) {
    if (StringUtils.startsWith(name, currentDirectory)) {
      String stripped = StringUtils.removeStart(name, currentDirectory);
      if (StringUtils.startsWith(stripped, DIRECTORY_SEPARATOR)) {
        return StringUtils.removeStart(stripped, DIRECTORY_SEPARATOR);
      }
      return stripped;
    }
    return name;
  }
}
