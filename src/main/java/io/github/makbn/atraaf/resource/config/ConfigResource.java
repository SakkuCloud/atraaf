package io.github.makbn.atraaf.resource.config;

import io.github.makbn.atraaf.api.common.AtraafResponse;
import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import io.github.makbn.atraaf.core.crud.UserCRUD;
import io.github.makbn.atraaf.core.entity.ApplicationEntity;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;
import io.github.makbn.atraaf.core.exception.AccessDeniedException;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


@CrossOrigin("*")
@RestController
@RequestMapping("/config")
public class ConfigResource {

    private final UserCRUD userCRUD;
    private final ConfigProvider configProvider;

    @Autowired
    public ConfigResource(UserCRUD userCRUD, ConfigProvider configProvider) {
        this.userCRUD = userCRUD;
        this.configProvider = configProvider;
    }


    @GetMapping("/{appId}/{envId}")
    public void downloadFile(@RequestHeader("key") String key, @RequestParam(value = "extension",required = true) String extension,
                             @PathVariable("appId")Long appId, @PathVariable("envId") Long envId,
                             HttpServletResponse response) throws ResourceNotFoundException, AccessDeniedException, InternalServerException, IOException {
        ApplicationEntity app = configProvider.getApplicationEntityById(appId);
        EnvironmentEntity ee = configProvider.getEnvironmentEntityById(envId);

        if(!ee.getKey().getAccessKey().equals(key)){
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }

        File file = configProvider.createConfigFile(app, ee, extension);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        StreamUtils.copy(new FileInputStream(file), response.getOutputStream());

    }

    @GetMapping("/{appId}/{envId}/map")
    public AtraafResponse<Map<String, String>> downloadFile(@RequestHeader("key") String key,
                                                            @PathVariable("appId") Long appId, @PathVariable("envId") Long envId,
                                                            HttpServletResponse response) throws ResourceNotFoundException, AccessDeniedException, InternalServerException, IOException {
        ApplicationEntity app = configProvider.getApplicationEntityById(appId);
        EnvironmentEntity ee = configProvider.getEnvironmentEntityById(envId);

        if (!ee.getKey().getAccessKey().equals(key)) {
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("not allowed")
                    .build();
        }

        return AtraafResponseImp.<Map<String, String>>builder()
                .result(configProvider.getConfigFileAsMap(app, ee))
                .code(HttpStatus.OK.value())
                .build();

    }

}
