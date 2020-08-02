package io.github.makbn.atraaf.core.exception;

import io.github.makbn.atraaf.api.common.AtraafResponse;
import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(basePackages = "io.github.makbn")
public class ExceptionHandler extends ResponseEntityExceptionHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value =
            {RuntimeException.class,
                    InvalidRequestException.class,
                    AccessDeniedException.class,
                    ResourceNotFoundException.class,
                    InterruptedException.class,
                    io.github.makbn.atraaf.core.exception.AccessDeniedException.class,
                    InternalServerException.class})
    public final ResponseEntity<?> handleExceptions(RuntimeException ex, WebRequest request) {


        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof InvalidRequestException) {
            HttpStatus status = HttpStatus.valueOf(((InvalidRequestException) ex).code);
            InvalidRequestException irex = (InvalidRequestException) ex;
            return handleInvalidRequestException(irex, headers, status, request);
        } else if (ex instanceof ResourceNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleExceptionInternal(ex, headers, status, request);
        } else if (ex instanceof AccessDeniedException || ex instanceof io.github.makbn.atraaf.core.exception.AccessDeniedException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleExceptionInternal(ex, headers, status, request);
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.error("Unknown exception type: " + ex.getClass().getName());
                ex.printStackTrace();
            }
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, headers, status, request);
        }
    }

    private ResponseEntity<?> handleInvalidRequestException(InvalidRequestException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, headers, status, request);
    }


    /**
     * A single place to customize the response body of all Exception types.
     *
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     *
     * @param ex      The exception
     * @param headers The headers for the response
     * @param status  The response status
     * @param request The current request
     */
    protected ResponseEntity<AtraafResponse> handleExceptionInternal(Exception ex,
                                                                 HttpHeaders headers, HttpStatus status,
                                                                 WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        AtraafResponse<String> body = AtraafResponseImp.<String>builder()
                .error(true)
                .message(ex.getMessage())
                .code(status.value())
                .build();


        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * handle access denied for preAuthorize
     *
     * @param request             httpServletRequest
     * @param httpServletResponse httpServletResponse
     * @param e                   exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.sendRedirect("/error/access-denied");
    }
}