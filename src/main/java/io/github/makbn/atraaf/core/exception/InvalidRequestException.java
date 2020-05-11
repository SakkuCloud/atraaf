package io.github.makbn.atraaf.core.exception;

import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class InvalidRequestException extends AtraafException {

    @Builder
    public InvalidRequestException(String message, int code) {
        super(message, code);
    }

    @Override
    public ResponseEntity getResponse() {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AtraafResponseImp.builder()
                        .error(true)
                        .message(getMessage())
                        .code(code)
                        .build());
    }
}
