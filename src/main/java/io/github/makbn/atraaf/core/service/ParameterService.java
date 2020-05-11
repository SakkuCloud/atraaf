package io.github.makbn.atraaf.core.service;

import io.github.makbn.atraaf.api.business.Parameter;
import io.github.makbn.atraaf.api.business.imp.ParameterImp;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;
import io.github.makbn.atraaf.core.entity.ParameterEntity;
import io.github.makbn.atraaf.core.entity.ValueEntity;

public class ParameterService {
    public static Parameter getParameterForEnvironment(ParameterEntity pe, EnvironmentEntity ee) {
        return ParameterImp.builder()
                .key(pe.getName())
                .value(pe.getValues()
                        .stream()
                        .filter(v -> v.getEnvironment().getId() == ee.getId())
                        .findFirst()
                        .orElse(ValueEntity.builder()
                                .raw("NOT_AVAILABLE")
                                .build())
                        .getRaw())
                .build();
    }
}
