package io.github.makbn.atraaf.api.business;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.business.imp.EnvironmentImp;

import java.util.Date;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value= EnvironmentImp.class, name = "EnvironmentImp"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Environment {
    long getId();
    String getName();
    Date created();
    Date updated();
    Set<Parameter> getParameters();
    String getAccessKey();
}
