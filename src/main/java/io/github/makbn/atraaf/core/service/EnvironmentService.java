package io.github.makbn.atraaf.core.service;

import io.github.makbn.atraaf.api.business.Environment;
import io.github.makbn.atraaf.api.business.Parameter;
import io.github.makbn.atraaf.api.business.imp.EnvironmentImp;
import io.github.makbn.atraaf.api.business.imp.ParameterImp;
import io.github.makbn.atraaf.core.entity.ApplicationEntity;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;
import io.github.makbn.atraaf.core.entity.ParameterEntity;
import io.github.makbn.atraaf.core.entity.ValueEntity;

import java.util.*;
import java.util.stream.Collectors;

public class EnvironmentService {

    public static Environment getEnvironment(EnvironmentEntity entity){
        return EnvironmentImp.builder()
                .created(entity.getCreated())
                .id(entity.getId())
                .name(entity.getName())
                .updated(entity.getUpdated())
                .accessKey(entity.getKey().getAccessKey())
                .build();
    }

    public static SortedSet<Environment> getEnvironments(Set<EnvironmentEntity> entities) {
        return entities.stream()
                .map(EnvironmentService::getEnvironment)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(Environment::getId))));
    }

    public static Environment getEnvironmentWithParam(EnvironmentEntity entity, ApplicationEntity application){
        return EnvironmentImp.builder()
                .created(entity.getCreated())
                .id(entity.getId())
                .name(entity.getName())
                .updated(entity.getUpdated())
                .parameters(getParameters(entity, application))
                .build();
    }

    private static Set<Parameter> getParameters(EnvironmentEntity environment, ApplicationEntity application) {
        return application.getParameters()
                .parallelStream()
                .map(p -> ParameterImp.builder()
                        .key(p.getName())
                        .value(findValue(p, environment))
                        .build())
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Parameter::getKey))));

    }

    private static String findValue(ParameterEntity p, EnvironmentEntity environment) {
        Optional<ValueEntity> value = p.getValues()
                .stream()
                .filter(v -> v.getEnvironment().equals(environment))
                .findAny();

        if(value.isPresent())
            return value.get().getRaw();

        return p.getValues().stream()
                .filter(ValueEntity::isDefault)
                .findFirst().map(ValueEntity::getRaw).orElse(null);
    }
}
