package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.ParameterEnvsReq;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ParameterEnvsReqImp implements ParameterEnvsReq {
    private String key;
    private Map<Long, String> envValues;
}
