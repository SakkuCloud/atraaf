package io.github.makbn.atraaf.core.exception;

import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@NoArgsConstructor
public class InternalServerException extends AtraafException {

    @Builder
    public InternalServerException(String message, int code) {
        super(message, code);
    }

    @Override
    public ResponseEntity getResponse() {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AtraafResponseImp.builder()
                        .error(true)
                        .message(getMessage())
                        .code(code)
                        .build());
    }
}
