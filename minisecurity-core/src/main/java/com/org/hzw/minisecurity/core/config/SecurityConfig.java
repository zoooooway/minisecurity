package com.org.hzw.minisecurity.core.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author hzw
 * @version 1.0
 * @date 2021/9/23 19:49
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 表单登录配置
                .formLogin( form -> {
                    form
                            /*
                            Specifies the URL to send users to if login is required.
                            指定该路径后会将该路径关联为其他路径的默认值 但如果其他路径已经设置了值则不会进行覆盖
                             具体关联路径见注释
                             */
                            .loginPage("/login")
                            // 该路径即为表单登录的请求路径
                            .loginProcessingUrl("/formLogin")
                            .passwordParameter("password")
                            .usernameParameter("username")
                            .successHandler(null)
                            .failureHandler(null);

                })
                // 特殊路径处理
                .authorizeHttpRequests( authorizeHttpRequests ->
                    authorizeHttpRequests
                            .antMatchers("/login")
                            .permitAll()
	   			);
    }
}
