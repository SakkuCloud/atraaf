package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.EnvironmentReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvironmentReqImp implements EnvironmentReq {
    private String name;
}
