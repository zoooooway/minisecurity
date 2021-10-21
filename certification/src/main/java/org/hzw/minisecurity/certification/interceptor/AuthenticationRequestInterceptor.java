//package org.hzw.minisecurity.certification.interceptor;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.hzw.minisecurity.common.domain.AjaxResult;
//import org.hzw.minisecurity.common.service.SysLoginService;
//import org.hzw.minisecurity.common.provider.TokenService;
//import org.hzw.minisecurity.common.util.ServletUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @author hzw
// * @version 1.0
// * @date 2021/10/12 19:39
// */
//@Slf4j
//@Deprecated
//public class AuthenticationRequestInterceptor implements HandlerInterceptor {
//    @Value("${minisecurity.authenticationURL}")
//    private String authenticationURL;
//    @Autowired
//    private SysLoginService sysLoginService;
//    @Autowired
//    private TokenService tokenService;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // execute authentication
//        String login = sysLoginService.login(request.getParameter("username"),
//                request.getParameter("password"),
//                request.getParameter("code"),
//                request.getParameter("uuid")
//        );
//        ServletUtils.renderString(response, login);
//        log.info("登录成功!{}", login);
//        return false;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        if (ex != null) {
//            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(ex.getMessage())));
//        }
//    }
//}
