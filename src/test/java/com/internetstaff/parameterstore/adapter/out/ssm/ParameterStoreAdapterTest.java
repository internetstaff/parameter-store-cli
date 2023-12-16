package com.internetstaff.parameterstore.adapter.out.ssm;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterTier;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Testcontainers
class ParameterStoreAdapterTest {
  @Container
  static LocalStackContainer localStackContainer =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5"))
          .withServices(LocalStackContainer.Service.SSM);

  private final SsmClient ssmClient = SsmClient.builder()
      .endpointOverride(localStackContainer.getEndpoint())
      .credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(localStackContainer.getAccessKey(), localStackContainer.getSecretKey())
      ))
      .region(Region.of(localStackContainer.getRegion()))
      .build();

  private final ParameterStoreAdapter parameterStoreAdapter = new ParameterStoreAdapter(ssmClient);

  private void createTestParameter(String name, String value) {
    ssmClient.putParameter(PutParameterRequest.builder()
        .name(name)
        .value(value)
        .type(ParameterType.SECURE_STRING)
        .tier(ParameterTier.INTELLIGENT_TIERING)
        .build());
  }

  @Test
  void getParameterMissing() {
    assertThat(parameterStoreAdapter.getParameter("Does Not Exist")).isEmpty();
  }

  @Test
  void getParametersNoMatch() {
    var actual = parameterStoreAdapter.getParameters(RandomStringUtils.randomAlphabetic(32));
    assertThat(actual).isEmpty();
  }

  @Test
  void getParameters() {
    var metadata = new ArrayList<ParameterStore.Metadata>();

    for (int i = 0; i < 10; i++) {
      metadata.add(ParameterStore.Metadata.builder()
          .name(RandomStringUtils.randomAlphabetic(24))
          .build());

      createTestParameter(metadata.get(i).name(), RandomStringUtils.randomAlphabetic(24));
    }

    var actual = parameterStoreAdapter.getParameters("");

    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedDate")
        .containsExactlyInAnyOrderElementsOf(metadata);

    actual = parameterStoreAdapter.getParameters(metadata.get(0).name());

    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedDate")
        .containsExactly(metadata.get(0));
  }

  @Test
  void getParameter() {
    var name = RandomStringUtils.randomAlphabetic(24);
    var value = RandomStringUtils.randomAlphabetic(24);

    createTestParameter(name, value);

    var actual = parameterStoreAdapter.getParameter(name).orElseThrow();

    var expected = ParameterStore.Parameter.builder()
        .name(name)
        .value(value)
        .type(ParameterType.SECURE_STRING.name())
        .build();

    assertThat(actual).usingRecursiveComparison().ignoringFieldsOfTypes(Instant.class).isEqualTo(expected);
  }

  @Test
  void copyParameter() {
    var name = RandomStringUtils.randomAlphabetic(24);
    var value = RandomStringUtils.randomAlphabetic(24);

    createTestParameter(name, value);

    var newName = RandomStringUtils.randomAlphabetic(24);

    var actual = parameterStoreAdapter.copyParameter(name, newName);

    assertThat(actual).isTrue();

    var expected = ParameterStore.Parameter.builder()
        .name(newName)
        .value(value)
        .type(ParameterType.SECURE_STRING.name())
        .build();

    var newParameter = parameterStoreAdapter.getParameter(newName);

    assertThat(newParameter).get().usingRecursiveComparison()
        .ignoringFieldsOfTypes(Instant.class)
        .isEqualTo(expected);
  }

  @Test
  void deleteParameter() {
    var name = RandomStringUtils.randomAlphabetic(24);
    var value = RandomStringUtils.randomAlphabetic(24);

    createTestParameter(name, value);

    assertThat(parameterStoreAdapter.getParameter(name)).isNotEmpty();

    var actual = parameterStoreAdapter.deleteParameter(name);

    assertThat(actual).isTrue();

    assertThat(parameterStoreAdapter.getParameter(name)).isEmpty();
  }

}