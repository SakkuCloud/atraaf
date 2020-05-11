package io.github.makbn.atraaf.api.request.imp;

import io.github.makbn.atraaf.api.request.ApplicationReq;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApplicationReqImp implements ApplicationReq {
    private String name;
    private String description;

}
