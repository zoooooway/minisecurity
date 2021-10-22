package org.hzw.minisecurity.certification.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 前后端分离时使用的身份验证成功处理器
 *
 * 已弃用 使用jwt进行验证的流程中并不需要该处理器
 * @author hzw
 * @version 1.0
 * @date 2021/10/5 19:00
 */
@Deprecated
@Slf4j
@Component
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(200);
        var mapper = new ObjectMapper();
        HashMap<Integer, String> result = new HashMap<>(2);
        response.getWriter().write(mapper.writeValueAsString(result));
        log.debug("登录成功：{}", authentication);
    }
}
