package com.org.hzw.minisecurity.core.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.jetbrains.annotations.NotNull;

/**
 * @author hzw
 * @version 1.0
 * @date 2021/9/23 19:49
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 表单登录时的用户名参数名
     */
    private static final String USERNAME_PARAMETER ="username";
    /**
     * 表单登录时的密码参数名
     */
    private static final String PASSWORD_PARAMETER ="password";

    /**
     * 表单登录时的处理路径
     */
    private static final String FORM_LOGIN_AUTHENTICATE_URL ="/login";
    /**
     * 表单登录时的请求路径
     */
    private static final String FORM_LOGIN_REQUEST_URL ="/formLogin";



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin(Customizer.withDefaults());
        http
                // 表单登录配置
                .formLogin(getFormLoginConfigurerCustomizer())
                .authorizeRequests(request -> {
                    request
                            .antMatchers(
                                    FORM_LOGIN_AUTHENTICATE_URL,
                                    FORM_LOGIN_REQUEST_URL
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(Customizer.withDefaults());
    }

    /**
     * configure those requests that are login by form
     * @return formLoginCustomizer的Customizer
     */
    @org.jetbrains.annotations.NotNull
    private Customizer<FormLoginConfigurer<HttpSecurity>> getFormLoginConfigurerCustomizer() {
        return form -> {
            form
                    /*
                    Specifies the URL to send users to if login is required.

                    指定该路径后会影响其他路径的默认值 但如果其他路径已经设置了值则不会进行覆盖
                    具体关联路径见注释
                     */
                    .loginPage(FORM_LOGIN_AUTHENTICATE_URL)
                    // 该路径即为表单登录的请求路径
                    .loginProcessingUrl(FORM_LOGIN_REQUEST_URL)
                    .usernameParameter(USERNAME_PARAMETER)
                    .passwordParameter(PASSWORD_PARAMETER)
                    .successHandler(null)
                    .failureHandler(null);
        };
    }

}
