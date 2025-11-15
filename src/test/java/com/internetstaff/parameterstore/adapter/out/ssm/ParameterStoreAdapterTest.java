package com.internetstaff.parameterstore.adapter.out.ssm;

import com.internetstaff.parameterstore.application.port.in.GetCurrentDirectoryUseCase;
import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class ParameterStoreAdapterTest {
  @Container
  static LocalStackContainer localStackContainer =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4"))
          .withServices(LocalStackContainer.Service.SSM, LocalStackContainer.Service.KMS);

  @Spy
  private SsmClient ssmClient = SsmClient.builder()
      .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.SSM))
      .credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(localStackContainer.getAccessKey(), localStackContainer.getSecretKey())
      ))
      .region(Region.of(localStackContainer.getRegion()))
      .build();
  @Mock
  private GetCurrentDirectoryUseCase currentDirectoryUseCase;
  @InjectMocks
  private ParameterStoreAdapter parameterStoreAdapter;

  private static String q(String name) {
    return name.startsWith("/") ? name : "/" + name;
  }

  @BeforeEach
  void setUp() {
    lenient().when(currentDirectoryUseCase.getCurrentDirectory()).thenReturn("/");
    lenient().when(currentDirectoryUseCase.qualifyName(anyString())).thenAnswer(inv -> inv.getArgument(0, String.class));
  }

  private void createTestParameter(String name, String value) {
    ssmClient.putParameter(PutParameterRequest.builder()
        .name(q(name))
        .value(value)
        .type(ParameterType.SECURE_STRING)
        .tier(ParameterTier.INTELLIGENT_TIERING)
        .build());
  }

  @Test
  void getParameterMissing() {
    assertThat(parameterStoreAdapter.getParameter(q("Does Not Exist"))).isEmpty();
  }

  @Test
  void getParametersNoMatch() {
    var actual = parameterStoreAdapter.getParameters(RandomStringUtils.insecure().nextAlphabetic(32));
    assertThat(actual).isEmpty();
  }

  @Test
  void getParameters() {
    var metadata = new ArrayList<ParameterStore.Metadata>();

    for (int i = 0; i < 10; i++) {
      metadata.add(ParameterStore.Metadata.builder()
          .name(RandomStringUtils.insecure().nextAlphabetic(24))
          .build());

      createTestParameter(metadata.get(i).name(), RandomStringUtils.insecure().nextAlphabetic(24));
    }

    var actual = parameterStoreAdapter.getParameters("");

    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedDate")
        .containsExactlyInAnyOrderElementsOf(metadata);

    actual = parameterStoreAdapter.getParameters(metadata.getFirst().name());

    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedDate")
        .containsExactly(metadata.getFirst());
  }

  @Test
  void getParameter() {
    var name = q(RandomStringUtils.insecure().nextAlphabetic(24));
    var value = RandomStringUtils.insecure().nextAlphabetic(24);

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
    var name = q(RandomStringUtils.insecure().nextAlphabetic(24));
    var value = RandomStringUtils.insecure().nextAlphabetic(24);

    createTestParameter(name, value);

    var newName = q(RandomStringUtils.insecure().nextAlphabetic(24));

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
  void createParameter() {
    var name = q(RandomStringUtils.insecure().nextAlphabetic(24));
    var value = RandomStringUtils.insecure().nextAlphabetic(24);

    parameterStoreAdapter.createParameter(name, value, "SecureString");

    var actual = ssmClient.getParameter(GetParameterRequest.builder()
            .name(q(name))
            .withDecryption(true)
            .build())
        .parameter();

    var expected = Parameter.builder()
        .name(name)
        .value(value)
        .version(1L)
        .dataType("text")
        .type(ParameterType.SECURE_STRING)
        .build();

    assertThat(actual).usingRecursiveComparison()
        .ignoringFields("lastModifiedDate", "arn")
        .isEqualTo(expected);
  }

  @Test
  void deleteParameter() {
    var name = q(RandomStringUtils.insecure().nextAlphabetic(24));
    var value = RandomStringUtils.insecure().nextAlphabetic(24);

    createTestParameter(name, value);

    assertThat(parameterStoreAdapter.getParameter(name)).isNotEmpty();

    var actual = parameterStoreAdapter.deleteParameter(name);

    assertThat(actual).isTrue();

    assertThat(parameterStoreAdapter.getParameter(name)).isEmpty();
  }

}