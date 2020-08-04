package io.github.makbn.atraaf.api.request;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.ParameterEnvsReqImp;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

@JsonTypeInfo(use = CLASS, defaultImpl = ParameterEnvsReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParameterEnvsReqImp.class, name = "ParameterEnvsReqImp"),
})
public interface ParameterEnvsReq {
    String getKey();

    Map<Long, String> getEnvValues();

    void setKey(String key);

    void setEnvValues(Map<Long, String> value);
}
