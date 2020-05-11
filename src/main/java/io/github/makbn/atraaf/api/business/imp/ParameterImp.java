package io.github.makbn.atraaf.api.business.imp;

import io.github.makbn.atraaf.api.business.Parameter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParameterImp implements Parameter {
    private String key;
    private String value;

}
