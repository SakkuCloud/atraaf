package io.github.makbn.atraaf.core.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final SecurityUtils securityUtils;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, SecurityUtils securityUtils) {
        super(authenticationManager);
        this.securityUtils = securityUtils;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(securityUtils.HEADER_STRING);

        if (header == null || !header.startsWith(securityUtils.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

    @SuppressWarnings("Duplicates")
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(securityUtils.HEADER_STRING);
        Algorithm algorithmHS = Algorithm.HMAC512(securityUtils.SECRET);

        if (token != null) {
            String user = null;
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            try {
                if (token.contains(securityUtils.TOKEN_PREFIX)) {
                    token = token.replace(securityUtils.TOKEN_PREFIX + " ", "");

                    if (StringUtils.isEmpty(token) || token.length() < 5)
                        return null;
                    DecodedJWT jwt = JWT.require(algorithmHS).build().verify(token);
                    Map<String, Claim> claims = jwt.getClaims();
                    user = jwt.getSubject();

                    List<String> roles = claims.get("role").asList(String.class);

                    for (String role : roles) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, token, authorities);
                }
            } catch (Exception e) {
                logger.error(getClass().getName(), e);
                return null;
            }

            return null;
        }
        return null;
    }
}
