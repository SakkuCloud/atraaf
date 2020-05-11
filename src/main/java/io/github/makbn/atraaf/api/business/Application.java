package io.github.makbn.atraaf.api.business;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.makbn.atraaf.api.business.imp.ApplicationImp;

import java.util.Date;
import java.util.Set;


import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value= ApplicationImp.class, name = "ApplicationImp"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Application {
    long getId();

    String getName();

    String getDescription();

    Date created();

    Date updated();

    Set<Environment> getEnvironments();
}
