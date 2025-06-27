package com.internetstaff.parameterstore.application;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentDirectoryServiceTest {
  @ParameterizedTest
  @CsvSource({
      "/,,/",
      "/etc,,/",
      "/,/etc,/etc",
      "/etc,java,/etc/java",
      "/etc,..,/",
      "/etc/java/jdk,../..,/etc",
      "/,/etc/java/..,/etc"
  })
  void testAbsolute(String initialDir, String command, String expectedDir) {
    var currentDirectoryService = new CurrentDirectoryService(initialDir);

    currentDirectoryService.setCurrentDirectory(command);

    assertThat(currentDirectoryService.getCurrentDirectory()).isEqualTo(expectedDir);
  }

  @ParameterizedTest
  @CsvSource({
      "'',testfile,/testfile",
      "/testdir,testfile,/testdir/testfile",
      "/testdir,../testfile,/testfile"
  })
  void testQualifyName(String currentDirectory, String name, String expectedName) {
    var currentDirectoryService = new CurrentDirectoryService(currentDirectory);

    var actual = currentDirectoryService.qualifyName(name);
    assertThat(actual).isEqualTo(expectedName);
  }

}