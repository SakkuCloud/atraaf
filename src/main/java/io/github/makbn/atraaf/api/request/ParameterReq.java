package io.github.makbn.atraaf.api.request;


import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.EnvironmentReqImp;
import io.github.makbn.atraaf.api.request.imp.ParameterReqImp;

@JsonTypeInfo(use = CLASS, defaultImpl = ParameterReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= ParameterReqImp.class, name = "ParameterReqImp"),
})
public interface ParameterReq {
    String getKey();
    String getValue();
    void setKey(String key);
    void setValue(String value);
    boolean isGlobal();
    void setGlobal(boolean glob);
}
