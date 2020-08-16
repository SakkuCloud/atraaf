package io.github.makbn.atraaf.resource.application;


import io.github.makbn.atraaf.api.business.Application;
import io.github.makbn.atraaf.api.business.Environment;
import io.github.makbn.atraaf.api.business.Parameter;
import io.github.makbn.atraaf.api.business.User;
import io.github.makbn.atraaf.api.common.AtraafResponse;
import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import io.github.makbn.atraaf.api.request.ApplicationReq;
import io.github.makbn.atraaf.api.request.EnvironmentReq;
import io.github.makbn.atraaf.api.request.ParameterEnvsReq;
import io.github.makbn.atraaf.api.request.ParameterReq;
import io.github.makbn.atraaf.api.request.imp.ParameterReqImp;
import io.github.makbn.atraaf.core.config.security.SecurityUtils;
import io.github.makbn.atraaf.core.crud.UserCRUD;
import io.github.makbn.atraaf.core.entity.*;
import io.github.makbn.atraaf.core.exception.AccessDeniedException;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.InvalidRequestException;
import io.github.makbn.atraaf.core.exception.ResourceNotFoundException;
import io.github.makbn.atraaf.core.service.ApplicationService;
import io.github.makbn.atraaf.core.service.EnvironmentService;
import io.github.makbn.atraaf.core.service.ParameterService;
import io.github.makbn.atraaf.core.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin("*")
@RestController
@RequestMapping("/application")
public class ApplicationResource {

    private final ApplicationProvider applicationProvider;
    private final UserCRUD userCRUD;
    private final SecurityUtils securityUtils;
    @Autowired
    public ApplicationResource(ApplicationProvider applicationProvider, UserCRUD userCRUD, SecurityUtils securityUtils) {
        this.applicationProvider = applicationProvider;
        this.userCRUD = userCRUD;
        this.securityUtils = securityUtils;
    }

    @PostMapping(value = {"/",""}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Application> createApplication(@RequestBody ApplicationReq req) throws InternalServerException {
        UserEntity userEntity = getUserEntity(true);
        ApplicationEntity app = applicationProvider.saveApplication(userEntity, req);

        return AtraafResponseImp.<Application>builder()
                .message("Ok")
                .result(ApplicationService.getApplication(app))
                .build();
    }

    @GetMapping(value = {"/",""}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<List<Application>> getAllApplications(){
        UserEntity user = getUserEntity(true);
        List<ApplicationEntity> apps = applicationProvider.getAllApplication(user);

        return AtraafResponseImp.<List<Application>>builder()
                .result(ApplicationService.getApplications(apps))
                .build();
    }

    @GetMapping(value = "/{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Application> getApplication(@PathVariable(value = "appId") Long id) throws ResourceNotFoundException, AccessDeniedException {
        User user = getUser(true);
        ApplicationEntity entity = applicationProvider.getApplicationEntityById(id);

        if(entity.getOwner().getId() == Objects.requireNonNull(user).getId() ||
                entity.getEnvironments()
                        .stream()
                        .anyMatch(e -> e.getCollaborators()
                                .stream()
                                .filter(c -> !c.isRemoved())
                                .map(CollaboratorEntity::getUser)
                                .anyMatch(u -> u.getId() == user.getId()))) {

            return AtraafResponseImp.<Application>builder()
                    .result(ApplicationService.getApplicationWithEnvironments(entity))
                    .code(HttpStatus.OK.value())
                    .build();
        }

        throw AccessDeniedException.builder()
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }


    @GetMapping(value = "/{appId}/{envId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Application> getEnvironment(@PathVariable(value = "appId") Long appId,
                                                      @PathVariable(value = "envId") Long envId) throws ResourceNotFoundException, AccessDeniedException {

        User user = getUser(true);
        ApplicationEntity entity = applicationProvider.getApplicationEntityById(appId);

        if(entity.getEnvironments().stream().noneMatch(e -> e.getId() == envId))
            throw ResourceNotFoundException.builder()
                    .message("environment not found")
                    .code(HttpStatus.NOT_FOUND.value())
                    .build();

        else if(entity.getOwner().getId() == Objects.requireNonNull(user).getId() ||
                entity.getEnvironments()
                        .stream()
                        .filter(e -> e.getId() == envId)
                        .anyMatch(e -> e.getCollaborators()
                                .stream()
                                .filter(c -> !c.isRemoved())
                                .map(CollaboratorEntity::getUser)
                                .anyMatch(u -> u.getId() == user.getId()))) {

            return AtraafResponseImp.<Application>builder()
                    .result(ApplicationService.getApplicationWithParams(entity, envId))
                    .code(HttpStatus.OK.value())
                    .build();
        }

        throw AccessDeniedException.builder()
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .code(HttpStatus.FORBIDDEN.value())
                .build();

    }

    @PostMapping(value = "/{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Environment> createEnvironment(@PathVariable(value = "appId") Long appId,
                                                         @RequestBody EnvironmentReq env)
            throws ResourceNotFoundException, AccessDeniedException, InvalidRequestException, InternalServerException {
        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if(app.getOwner().getId() != Objects.requireNonNull(user).getId()){
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }

        if(env == null || env.getName() == null)
            throw InvalidRequestException.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("environment is required")
                    .build();

        EnvironmentEntity environmentEntity = applicationProvider.addEnvironmentToApplication(app, env);

        return AtraafResponseImp.<Environment>builder()
                .code(HttpStatus.CREATED.value())
                .result(EnvironmentService.getEnvironment(environmentEntity))
                .build();
    }

    @DeleteMapping(value = "/{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Boolean> removeApplication(@PathVariable(value = "appId") Long appId)
            throws ResourceNotFoundException, AccessDeniedException, InvalidRequestException, InternalServerException {
        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if (app.getOwner().getId() != Objects.requireNonNull(user).getId()) {
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }

        applicationProvider.removeApplication(app);

        return AtraafResponseImp.<Boolean>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .result(true)
                .build();
    }
    // app/appid/env/envid was better!
    // app/appid/param/paramId was better!

    @DeleteMapping(value = "/{appId}/{envId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Boolean> removeEnvironment(@PathVariable(value = "appId") Long appId,
                                                     @PathVariable(value = "envId") Long envId
    )
            throws ResourceNotFoundException, AccessDeniedException, InvalidRequestException, InternalServerException {
        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if (app.getOwner().getId() != Objects.requireNonNull(user).getId()) {
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }
        EnvironmentEntity ee = applicationProvider.getEnvironmentEntityById(envId, appId);

        applicationProvider.removeEnvironment(ee);

        return AtraafResponseImp.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .result(true)
                .build();
    }

    @DeleteMapping(value = "/{appId}/params", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Boolean> removeParameter(@PathVariable(value = "appId") Long appId,
                                                   @RequestParam(value = "name") String paramName
    )
            throws ResourceNotFoundException, AccessDeniedException, InvalidRequestException, InternalServerException {
        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if (app.getOwner().getId() != Objects.requireNonNull(user).getId()) {
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }
        ParameterEntity pe = applicationProvider.getParameterEntityByName(paramName, app);

        applicationProvider.removeParameter(pe);

        return AtraafResponseImp.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .result(true)
                .build();
    }


    @PostMapping(value = "/{appId}/{envId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public AtraafResponse<Set<Parameter>> createOrUpdateParameter(@PathVariable(value = "appId") Long appId,
                                                                  @PathVariable(value = "envId") Long envId,
                                                                  @RequestBody Set<ParameterReq> parameterReq) throws ResourceNotFoundException, AccessDeniedException {

        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if(app.getOwner().getId() != Objects.requireNonNull(user).getId()){
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }

        Validate.noNullElements(parameterReq,"one of parameters is null or invalid");
        EnvironmentEntity ee = applicationProvider.getEnvironmentEntityById(envId, appId);
        Set<Parameter> parameters = parameterReq.stream()
                .filter(p -> StringUtils.isNotEmpty(p.getKey()))
                .map(p -> applicationProvider.saveOrUpdateParam(app, p, ee))
        .map(pe -> ParameterService.getParameterForEnvironment(pe, ee))
        .collect(Collectors.toSet());

        return AtraafResponseImp.<Set<Parameter>>builder()
                .code(HttpStatus.CREATED.value())
                .result(parameters)
                .build();
    }

    @PostMapping(value = "/{appId}/params", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")//wrong envIds will be ignored
    public AtraafResponse<Boolean> createOrUpdateParameter(@PathVariable(value = "appId") Long appId,
                                                           @RequestBody Set<ParameterEnvsReq> parameterReq) throws ResourceNotFoundException, AccessDeniedException {

        User user = getUser(true);
        ApplicationEntity app = applicationProvider.getApplicationEntityById(appId);
        if (app.getOwner().getId() != Objects.requireNonNull(user).getId()) {
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }
        Validate.noNullElements(parameterReq, "one of parameters is null or invalid");

        for (ParameterEnvsReq p : parameterReq) {
            if (StringUtils.isNotEmpty(p.getKey())) {
                for (Map.Entry<Long, String> envValueEntity : p.getEnvValues().entrySet()) {

                    EnvironmentEntity ee = applicationProvider.getEnvironmentEntityById(envValueEntity.getKey(), appId);
                    applicationProvider.saveOrUpdateParam(app, ParameterReqImp.builder()
                            .key(p.getKey())
                            .value(envValueEntity.getValue())
                            .global(false)
                            .build(), ee);
                    app = applicationProvider.getApplicationEntityById(appId);
                }
            }
        }

        return AtraafResponseImp.<Boolean>builder()
                .code(HttpStatus.CREATED.value())
                .result(true)
                .build();
    }


    /**
     * retrieve user from security context
     *
     * @return current User entity
     * @throws AccessDeniedException if user not found in database
     */
    public UserEntity getUserEntity(boolean required) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<UserEntity> user = userCRUD.findUserById(Long.valueOf(userId));
        if(user.isPresent())
            return user.get();
        else if (required)
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("user not found!")
                    .build();
        else
            return null;
    }


    private User getUser(boolean required){
        return UserService.getUser(getUserEntity(required));
    }


}
