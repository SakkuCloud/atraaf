package io.github.makbn.atraaf.api.request;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.request.imp.ApplicationReqImp;

@JsonTypeInfo(use = CLASS, defaultImpl = ApplicationReqImp.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= ApplicationReqImp.class, name = "ApplicationReqImp"),
})
public interface ApplicationReq {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String desc);
}
