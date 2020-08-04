package io.github.makbn.atraaf.core.service;

import io.github.makbn.atraaf.api.business.Application;
import io.github.makbn.atraaf.api.business.imp.ApplicationImp;
import io.github.makbn.atraaf.core.entity.ApplicationEntity;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationService {

    public static Application getApplication(ApplicationEntity entity){
        return ApplicationImp.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .description(entity.getDescription())
                .name(entity.getName())
                .build();
    }


    public static List<Application> getApplications(List<ApplicationEntity> entities){
        return entities.parallelStream()
                .map(ApplicationService::getApplication)
                .sorted(Comparator.comparingLong(Application::getId))
                .collect(Collectors.toList());
    }

    public static Application getApplicationWithEnvironments(ApplicationEntity entity){
        return ApplicationImp.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .description(entity.getDescription())
                .name(entity.getName())
                .environments(EnvironmentService.getEnvironments(entity.getEnvironments()))
                .build();
    }

    public static Application getApplicationWithParams(ApplicationEntity entity, Long environmentId){
        return ApplicationImp.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .description(entity.getDescription())
                .name(entity.getName())
                .environments(Collections.singleton(EnvironmentService.getEnvironmentWithParam(findEnvironment(entity, environmentId), entity)))
                .build();
    }

    private static EnvironmentEntity findEnvironment(ApplicationEntity entity, Long environmentId) {
        return entity.getEnvironments()
                .parallelStream()
                .filter(e -> e.getId() == environmentId)
                .findFirst()
                .get();
    }
}
