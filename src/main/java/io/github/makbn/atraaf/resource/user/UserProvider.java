package io.github.makbn.atraaf.resource.user;

import io.github.makbn.atraaf.api.request.LoginReq;
import io.github.makbn.atraaf.api.request.SignUpReq;
import io.github.makbn.atraaf.core.config.security.SecurityUtils;
import io.github.makbn.atraaf.core.crud.UserCRUD;
import io.github.makbn.atraaf.core.entity.Role;
import io.github.makbn.atraaf.core.entity.UserEntity;
import io.github.makbn.atraaf.core.exception.AccessDeniedException;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

@Component
public class UserProvider {
    private final UserCRUD userCRUD;
    private final SecurityUtils securityUtils;

    @Autowired
    public UserProvider(UserCRUD userCRUD, SecurityUtils securityUtils) {
        this.userCRUD = userCRUD;
        this.securityUtils = securityUtils;
    }


    public String signUpUser(SignUpReq signUpReq) {

        Validate.notNull(signUpReq.getEmail(), "email parameter is required");
        Validate.notNull(signUpReq.getPassword(), "password parameter is required");
        Validate.notNull(signUpReq.getUsername(), "username parameter is required");

        UserEntity userEntity = UserEntity.builder()
                .email(signUpReq.getEmail())
                .password(signUpReq.getPassword())
                .username(signUpReq.getUsername())
                .roles(Collections.singleton(Role.ROLE_USER))
                .ssoId(signUpReq.getSsoid())
                .build();

        Long userid = userCRUD.createUser(userEntity)
                .filter(id -> id > 0)
                .orElseThrow(() -> InternalServerException.builder().build());
        userEntity.setId(userid);
        return securityUtils.generateToken(userEntity, signUpReq.getPodToken());
    }

    //todo move toadminsection and implement emdpoint
    public String signUpUserByAdmin(SignUpReq signUpReq) {

        Validate.notNull(signUpReq.getEmail(), "email parameter is required");
        Validate.notNull(signUpReq.getPassword(), "password parameter is required");
        Validate.notNull(signUpReq.getUsername(), "username parameter is required");

        UserEntity userEntity = UserEntity.builder()
                .email(signUpReq.getEmail())
                .password(signUpReq.getPassword())
                .username(signUpReq.getUsername())
                .roles(CollectionUtils.isEmpty(signUpReq.getRoles()) ?
                        Collections.singleton(Role.ROLE_USER) :
                        signUpReq.getRoles())
                .ssoId(signUpReq.getSsoid())
                .build();

        Long userid = userCRUD.createUser(userEntity)
                .filter(id -> id > 0)
                .orElseThrow(() -> InternalServerException.builder().build());
        userEntity.setId(userid);
        return securityUtils.generateToken(userEntity, signUpReq.getPodToken());
    }

    public String getNewToken(LoginReq loginReq, String podToken) {

        Validate.notNull(loginReq.getPassword(), "password parameter is required");
        Validate.notNull(loginReq.getUsername(), "username parameter is required");
        Validate.notNull(podToken, "podToken parameter is required");

        UserEntity userEntity = userCRUD.findUserByLoginInfo(loginReq)
                .filter(u -> u.getId() > 0)
                .orElseThrow(() -> AccessDeniedException.builder().build());//todo return 401 (auth exp)
        return securityUtils.generateToken(userEntity, podToken);
    }
}
