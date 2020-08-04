package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.ParameterReq;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParameterReqImp implements ParameterReq {
    private String key;
    private String value;
    private boolean global;
}
