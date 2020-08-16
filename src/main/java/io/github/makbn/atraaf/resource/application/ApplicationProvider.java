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

    public void removeApplication(ApplicationEntity applicationEntity) {
        applicationCRUD.removeApplication(applicationEntity);
    }

    public void removeEnvironment(EnvironmentEntity environmentEntity) {
        applicationCRUD.removeEnvironment(environmentEntity);
    }

    public void removeParameter(ParameterEntity parameterEntity) {
        applicationCRUD.removeParameter(parameterEntity);
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
                    .code(HttpStatus.CONFLICT.value())
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

    public EnvironmentEntity getEnvironmentEntityById(Long envId, Long appId) throws ResourceNotFoundException {
        Validate.notNull(envId, "envId parameter is required");
        Validate.notNull(appId, "appId parameter is required");
        EnvironmentEntity ee = applicationCRUD.findEnvironmentById(envId)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("environment " + envId + " not found")
                        .build());
        if (ee.getApplication().getId() != appId)
            throw ResourceNotFoundException.builder()
                    .message("environment " + envId + " not found for this application")
                    .build();
        return ee;
    }


    public ParameterEntity getParameterEntityByName(String paramName, ApplicationEntity app) throws ResourceNotFoundException {
        Validate.notNull(paramName, "paramName parameter is required");
        return applicationCRUD.findParameterByName(paramName, app)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("parameter " + paramName + " not found for this application")
                        .build());
    }


    public ParameterEntity saveOrUpdateParam(ApplicationEntity app, ParameterReq p, EnvironmentEntity ee) throws InternalServerException {

        for (ParameterEntity appParam : app.getParameters()) {
            if (appParam.getName().equals(p.getKey())) {
                return addValueToParam(appParam, p, ee);
            }
        }
        return saveParam(p, ee);
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

    public ParameterEntity addValueToParam(ParameterEntity appParam, ParameterReq p, EnvironmentEntity ee) throws InternalServerException {

        if (p.isGlobal())
            throw new InvalidRequestException("you cant set global field for existing param!", 406);

        if (appParam.getValues() == null) {
            appParam.setValues(new HashSet<>());
        }

        //edit value
        for (ValueEntity value : appParam.getValues()) {
            if (value.getEnvironment().getId() == ee.getId()) {
                value.setRaw(p.getValue());
                applicationCRUD.updateParameter(appParam);
                return appParam;
            }
        }

        //save new value (in new ee) for existing param :)
        appParam.getValues().add(ValueEntity.builder()
                .environment(ee)
                .parameter(appParam)
                .isDefault(false)
                .raw(p.getValue())
                .build());
        applicationCRUD.updateParameter(appParam);
        return appParam;
    }
}
