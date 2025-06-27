package com.internetstaff.parameterstore.application.port.in;

public interface GetCurrentDirectoryUseCase {
  String getCurrentDirectory();

  String qualifyName(String name);
}
