package io.github.makbn.atraaf.api.request;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.LoginReqImp;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

@JsonTypeInfo(use = CLASS, defaultImpl = LoginReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginReq.class, name = "LoginReqImp"),
})
public interface LoginReq {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getPodToken();

    void setPodToken(String podToken);

}
