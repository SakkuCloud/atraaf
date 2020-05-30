package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.SignUpReq;
import io.github.makbn.atraaf.core.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpReqImp implements SignUpReq {
    private String email;
    private String username;
    private String ssoid;
    private Set<Role> roles;
    private String podToken;
    private String password;

}
