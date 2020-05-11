package io.github.makbn.atraaf.api.request;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.ApplicationReqImp;
import io.github.makbn.atraaf.api.request.imp.EnvironmentReqImp;

@JsonTypeInfo(use = CLASS, defaultImpl = EnvironmentReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= EnvironmentReqImp.class, name = "EnvironmentReqImp"),
})
public interface EnvironmentReq {
    String getName();
    void setName(String name);
}
