package org.hzw.minisecurity.certification.handler;

import com.alibaba.fastjson.JSON;
import org.hzw.minisecurity.common.constant.HttpStatus;
import org.hzw.minisecurity.common.domain.AjaxResult;
import org.hzw.minisecurity.common.util.ServletUtils;
import org.hzw.minisecurity.common.util.text.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理认证失败
 * @author hzw
 * @version 1.0
 * @date 2021/10/10 16:41
 */
@Component
public class FailureAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
