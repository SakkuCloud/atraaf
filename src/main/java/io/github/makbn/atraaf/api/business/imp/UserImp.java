package io.github.makbn.atraaf.api.business.imp;

import io.github.makbn.atraaf.api.business.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserImp implements User {

    private long id;
    private String username;
    private String email;
}
