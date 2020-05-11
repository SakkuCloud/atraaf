package io.github.makbn.atraaf.api.common.imp;

import io.github.makbn.atraaf.api.common.AtraafResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

@Builder
@Getter
public class AtraafResponseImp<T> implements AtraafResponse<T> {
    private final int code;
    private final boolean error;
    private final T result;
    @Builder.Default
    private final String message = "Ok";
    @Builder.Default
    private final Date time = Calendar.getInstance().getTime();

}
