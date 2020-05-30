package io.github.makbn.atraaf.resource.application;

import io.github.makbn.atraaf.api.request.ApplicationReq;
import io.github.makbn.atraaf.api.request.EnvironmentReq;
import io.github.makbn.atraaf.api.request.ParameterReq;
import io.github.makbn.atraaf.core.crud.ApplicationCRUD;
import io.github.makbn.atraaf.core.entity.*;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.InvalidRequestException;
import io.github.makbn.atraaf.core.exception.ResourceNotFoundException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApplicationProvider {

    private final ApplicationCRUD applicationCRUD;

    @Autowired
    public ApplicationProvider(ApplicationCRUD applicationCRUD) {
        this.applicationCRUD = applicationCRUD;
    }

    ApplicationEntity saveApplication(UserEntity userEntity, ApplicationReq applicationReq) throws InternalServerException {
        Validate.notNull(applicationReq.getName(), "name parameter is required");
        ApplicationEntity applicationEntity = ApplicationEntity.builder()
                .name(applicationReq.getName())
                .description(applicationReq.getDescription())
                .owner(userEntity)
                .build();

        Long appId = applicationCRUD.createApplication(applicationEntity)
                .filter(id -> id > 0)
                .orElseThrow(() -> InternalServerException.builder().build());

        applicationEntity.setId(appId);
        return applicationEntity;
    }


    ApplicationEntity getApplicationEntityById(Long id) throws ResourceNotFoundException {
        Validate.notNull(id, "id parameter is required");

        Optional<ApplicationEntity> entity = applicationCRUD.findApplicationById(id);

        return entity.orElseThrow(() -> ResourceNotFoundException.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("application not found")
                .build());

    }


    List<ApplicationEntity> getAllApplication(UserEntity user) {
        Validate.notNull(user, "user parameter is required");
        return applicationCRUD.getAllApplications(user);
    }

    public EnvironmentEntity addEnvironmentToApplication(ApplicationEntity app, EnvironmentReq env) throws InternalServerException, InvalidRequestException {
        if(app.getEnvironments()
                .parallelStream()
                .anyMatch(e -> e.getName().toLowerCase().equals(env.getName().toLowerCase().trim()))){
            throw InvalidRequestException.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("environment's name is taken")
                    .build();
        }

        EnvironmentEntity environmentEntity = EnvironmentEntity.builder()
                .application(app)
                .key(AccessKeyEntity.builder()
                        .accessKey(UUID.randomUUID().toString())
                        .build())
                .name(env.getName())
                .build();

        Long envId = applicationCRUD.createEnvironment(environmentEntity)
                .filter(id -> id > 0)
                .orElseThrow(() -> InternalServerException.builder().build());

        environmentEntity.setId(envId);
        return environmentEntity;
    }

    public EnvironmentEntity getEnvironmentEntityById(Long envId) throws ResourceNotFoundException {
        Validate.notNull(envId, "envId parameter is required");
        return applicationCRUD.findEnvironmentById(envId)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("environment not found")
                        .build());
    }

    public ParameterEntity saveParam(ParameterReq p, EnvironmentEntity ee) throws InternalServerException {
        ParameterEntity parameterEntity = ParameterEntity.builder()
                .name(p.getKey())
                .application(ee.getApplication())
                .type(ParameterEntity.ParamType.TEXT)
                .build();

        if(p.isGlobal()){
            Set<ValueEntity> values = ee.getApplication()
                    .getEnvironments()
                    .stream()
                    .map(e -> ValueEntity.builder()
                            .environment(e)
                            .isDefault(true)
                            .parameter(parameterEntity)
                            .raw(p.getValue())
                            .build())
                    .collect(Collectors.toSet());
            parameterEntity.setValues(values);
        }else {
            parameterEntity.setValues(Collections.singleton(ValueEntity.builder()
                    .environment(ee)
                    .parameter(parameterEntity)
                    .isDefault(false)
                    .raw(p.getValue())
                    .build()));
        }

        Long paramId = applicationCRUD.createParameter(parameterEntity)
                .filter(id -> id > 0)
                .orElseThrow(() -> InternalServerException.builder().build());
        parameterEntity.setId(paramId);

        return parameterEntity;
    }
}
