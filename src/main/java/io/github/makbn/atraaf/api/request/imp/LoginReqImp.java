package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.LoginReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReqImp implements LoginReq {
    private String username;
    private String password;
    private String podToken;
}
