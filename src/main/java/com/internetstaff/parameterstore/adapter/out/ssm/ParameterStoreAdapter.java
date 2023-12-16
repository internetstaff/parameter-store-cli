package com.internetstaff.parameterstore.adapter.out.ssm;

import com.internetstaff.parameterstore.application.port.out.ParameterStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ParameterStoreAdapter implements ParameterStore {
  private final SsmClient ssmClient;

  @Override
  public List<Metadata> getParameters(String glob) {
    var request = DescribeParametersRequest.builder()
        .build();

    var result = new ArrayList<ParameterMetadata>();

    var noGlob = StringUtils.isBlank(glob);

    do {
      var response = ssmClient.describeParameters(request);
      result.addAll(response.parameters());
      request = request.toBuilder()
          .nextToken(response.nextToken())
          .build();
    } while (StringUtils.isNotBlank(request.nextToken()));

    return result.stream()
        .filter(parameter -> noGlob || FilenameUtils.wildcardMatch(parameter.name(), glob))
        .sorted(Comparator.comparing(ParameterMetadata::name))
        .map(metaData -> Metadata.builder()
            .name(metaData.name())
            .lastModifiedDate(metaData.lastModifiedDate())
            .build())
        .toList();
  }

  @Override
  public boolean copyParameter(String source, String destination) {
    var getRequest = GetParameterRequest.builder()
        .name(source)
        .withDecryption(true)
        .build();

    try {
      var parameter = ssmClient.getParameter(getRequest).parameter();

      var putRequest = PutParameterRequest.builder()
          .name(destination)
          .value(parameter.value())
          .type(parameter.type())
          .tier(ParameterTier.INTELLIGENT_TIERING)
          .build();

      ssmClient.putParameter(putRequest);
    } catch (ParameterNotFoundException e) {
      return false;
    }

    return true;
  }

  @Override
  public Optional<Parameter> getParameter(String name) {
    var request = GetParameterRequest.builder()
        .name(name)
        .withDecryption(true)
        .build();

    try {
      return Optional.of(ssmClient.getParameter(request).parameter())
          .map(p -> Parameter.builder()
              .name(p.name())
              .type(p.type().name())
              .value(p.value())
              .lastModifiedDate(p.lastModifiedDate())
              .build());

    } catch (ParameterNotFoundException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean deleteParameter(String name) {
    var deleteRequest = DeleteParameterRequest.builder()
        .name(name)
        .build();

    try {
      ssmClient.deleteParameter(deleteRequest);
    } catch (ParameterNotFoundException e) {
      return false;
    }

    return true;
  }

}
