package io.github.makbn.atraaf.api.request;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.SignUpReqImp;
import io.github.makbn.atraaf.core.entity.Role;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

@JsonTypeInfo(use = CLASS, defaultImpl = SignUpReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SignUpReq.class, name = "SignUpReqImp"),
})
public interface SignUpReq {

    String getUsername();

    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    Set<Role> getRoles();

    void setRoles(Set<Role> roles);

    String getPodToken();

    void setPodToken(String podToken);

    String getSsoid();

    void setSsoid(String ssoid);

    String getPassword();

    void setPassword(String password);
}
