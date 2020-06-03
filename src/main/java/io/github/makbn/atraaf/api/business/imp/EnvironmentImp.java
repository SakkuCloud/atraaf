package io.github.makbn.atraaf.api.business.imp;

import io.github.makbn.atraaf.api.business.Environment;
import io.github.makbn.atraaf.api.business.Parameter;
import lombok.Builder;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Builder
@Getter
public class EnvironmentImp implements Environment {
    private long id;
    private String name;
    @Builder.Default
    private Date created = Calendar.getInstance().getTime();
    @Builder.Default
    private Date updated = Calendar.getInstance().getTime();
    private Set<Parameter> parameters;
    private String accessKey;

    @Override
    public Date created() {
        return created;
    }

    @Override
    public Date updated() {
        return updated;
    }

}
