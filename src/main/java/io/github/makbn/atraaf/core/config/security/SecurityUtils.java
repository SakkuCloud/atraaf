package io.github.makbn.atraaf.core.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.makbn.atraaf.core.entity.UserEntity;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component("securityUtils")
@Configuration
@Configurable
@ComponentScan(basePackages = "io.github.makbn.atraaf.core.config.security")
public class SecurityUtils {

    public static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(30); // 30 days

    public static final String SIGN_UP_URL = "users/sign-up";
    public static final String LOGIN_URL = "users/login";

    @Value("${security.signing-key}")
    String SECRET;

    @Value("${security.jwt.token_prefix}")
    public String TOKEN_PREFIX;

    @Value("${security.jwt.header_name}")
    String HEADER_STRING;

    public String validateToken(String token) {

        Algorithm algorithmHS = Algorithm.HMAC512(SECRET);

        if (token != null) {

            try {
                token = token.replace(TOKEN_PREFIX + " ", "");
                DecodedJWT jwt = JWT.require(algorithmHS).build().verify(token);
                String user = jwt.getSubject();
                if (user != null) {
                    return user;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }
        return null;
    }

    @Bean
    @Primary
    public SecurityUtils getSecurityUtils() {
        return new SecurityUtils();
    }


    public String generateToken(UserEntity user, String podToken) {

        /*if (!user.isActive())
            throw AccessDeniedException.getInstance(AuthMessage.accountNotActive());
        */
        Algorithm algorithmHS = Algorithm.HMAC512(SECRET);

        String token = JWT.create()
                .withIssuer("ATRAAF-ISS")
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withArrayClaim("role", (String[]) user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toList())
                        .toArray(new String[]{}))
                .withClaim("sso_token", podToken)
                .sign(algorithmHS);

        return token;
    }
}
