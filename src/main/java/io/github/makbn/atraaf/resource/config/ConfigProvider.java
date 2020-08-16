package io.github.makbn.atraaf.resource.config;

import io.github.makbn.atraaf.core.crud.ApplicationCRUD;
import io.github.makbn.atraaf.core.entity.ApplicationEntity;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;
import io.github.makbn.atraaf.core.entity.ParameterEntity;
import io.github.makbn.atraaf.core.entity.ValueEntity;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.ResourceNotFoundException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Component
public class ConfigProvider {
    private final ApplicationCRUD applicationCRUD;

    @Autowired
    public ConfigProvider(ApplicationCRUD applicationCRUD) {
        this.applicationCRUD = applicationCRUD;
    }


    public ApplicationEntity getApplicationEntityById(Long appId) throws ResourceNotFoundException {
        Validate.notNull(appId, "id parameter is required");

        Optional<ApplicationEntity> entity = applicationCRUD.findApplicationById(appId);

        return entity.orElseThrow(() -> ResourceNotFoundException.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("application not found")
                .build());
    }

    public EnvironmentEntity getEnvironmentEntityById(Long envId) throws ResourceNotFoundException {
        Validate.notNull(envId, "envId parameter is required");
        return applicationCRUD.findEnvironmentById(envId)
                .orElseThrow(() -> ResourceNotFoundException.builder().build());
    }

    public File createConfigFile(ApplicationEntity app, EnvironmentEntity ee, String extension) throws InternalServerException {
        try {
            File configFile = File.createTempFile(app.getName(), extension != null ?
                    "." + extension :
                    ".properties");
            FileWriter fileWriter = new FileWriter(configFile);
            app.getParameters()
                    .stream()
                    .sorted(Comparator.comparing(ParameterEntity::getName))
                    .forEach(p -> {
                        Optional<ValueEntity> ve = p.getValues()
                                .stream()
                                .filter(v -> v.getEnvironment().getId() == ee.getId())
                                .findFirst();
                        if (ve.isPresent()) {
                            try {
                                fileWriter.write(p.getName() + "=" + ve.get().getRaw() + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            fileWriter.close();
            return configFile;
        } catch (IOException e) {
            throw InternalServerException.builder()
                    .message(e.getMessage())
                    .code(5001)
                    .build();
        }
    }

    public SortedMap<String, String> getConfigFileAsMap(ApplicationEntity app, EnvironmentEntity ee) {
        SortedMap<String, String> res = new TreeMap<>();
        app.getParameters()
                .forEach(p -> {
                    Optional<ValueEntity> ve = p.getValues()
                            .stream()
                            .filter(v -> v.getEnvironment().getId() == ee.getId())
                            .findFirst();
                    ve.ifPresent(valueEntity -> res.put(p.getName(), valueEntity.getRaw()));
                });
        return res;
    }
}
