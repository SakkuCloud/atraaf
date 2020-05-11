package io.github.makbn.atraaf.api.common;


import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonSubTypes({
        @JsonSubTypes.Type(value= AtraafResponseImp.class, name = "AtraafResponseImp"),
})
public interface AtraafResponse<T> {
    int getCode();

    boolean isError();

    T getResult();

    String getMessage();

    Date getTime();

}
