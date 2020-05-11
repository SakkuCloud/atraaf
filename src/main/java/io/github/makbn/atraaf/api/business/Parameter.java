package io.github.makbn.atraaf.api.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.business.imp.ParameterImp;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value= ParameterImp.class, name = "ParameterImp"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Parameter {
    String getKey();
    String getValue();
}
