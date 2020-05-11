package io.github.makbn.atraaf.core.service;

import io.github.makbn.atraaf.api.business.User;
import io.github.makbn.atraaf.api.business.imp.UserImp;
import io.github.makbn.atraaf.core.entity.UserEntity;

public class UserService {

    public static User getUser(UserEntity userEntity){
        return UserImp.builder()
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .id(userEntity.getId())
                .build();
    }
}
