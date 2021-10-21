package org.hzw.minisecurity.certification.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 提供简单post请求携带认证信息认证的过滤器
 *
 * @author hzw
 * @version 1.0
 * @date 2021/9/27 19:27
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleRequestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest;
        try (ServletInputStream inputStream = request.getInputStream()) {
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            String username = Optional.ofNullable(jsonNode.get("username")).orElseThrow(() -> new BadCredentialsException("未找到用户名或密码")).textValue();
            String password = Optional.ofNullable(jsonNode.get("password")).orElseThrow(() -> new BadCredentialsException("未找到用户名或密码")).textValue();
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
        } catch (IOException e) {
            log.debug("未找到用户名和密码", e);
            throw new BadCredentialsException("未找到用户名和密码");
        }
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
