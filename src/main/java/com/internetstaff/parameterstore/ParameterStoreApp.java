package com.internetstaff.parameterstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.annotation.CommandScan;
import software.amazon.awssdk.services.ssm.SsmClient;

@SpringBootApplication
@CommandScan
class ParameterStoreApp {

  public static void main(String[] args) {
    SpringApplication.run(ParameterStoreApp.class, args)
        .close();
  }

  @Bean
  SsmClient ssmClient() {
    return SsmClient.create();
  }

}
