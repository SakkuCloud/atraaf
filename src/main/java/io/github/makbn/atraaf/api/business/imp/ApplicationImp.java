package io.github.makbn.atraaf.api.business.imp;

import io.github.makbn.atraaf.api.business.Application;
import io.github.makbn.atraaf.api.business.Environment;
import lombok.Builder;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;


@Builder
@Getter
public class ApplicationImp implements Application {
    private long id;
    private String name;
    private String description;
    @Builder.Default
    private Date created = Calendar.getInstance().getTime();
    @Builder.Default
    private Date updated = Calendar.getInstance().getTime();
    private Set<Environment> environments;

    @Override
    public Date created() {
        return created;
    }

    @Override
    public Date updated() {
        return updated;
    }
}
