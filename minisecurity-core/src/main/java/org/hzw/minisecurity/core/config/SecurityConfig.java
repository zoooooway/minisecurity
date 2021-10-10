package org.hzw.minisecurity.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzw.minisecurity.core.filter.JwtAuthenticationTokenFilter;
import org.hzw.minisecurity.core.filter.SimpleRequestAuthenticationFilter;
import org.hzw.minisecurity.core.handler.JsonAuthenticationFailureHandler;
import org.hzw.minisecurity.core.handler.JsonAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

/**
 * @author hzw
 * @version 1.0
 * @date 2021/9/23 19:49
 */
@EnableWebSecurity(debug = true)
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


    @Autowired
    private DataSource dataSource;
    @Autowired
    private JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler;
    @Autowired
    private JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private ObjectMapper objectMapper = new ObjectMapper();
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();



    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin(Customizer.withDefaults());
        http
                // 表单登录配置
                .formLogin(getFormLoginConfigurerCustomizer())
                // 请求路径的安全校验配置
                .authorizeRequests(getExpressionInterceptUrlRegistryCustomizer())
//                .addFilterAt(simpleRequestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable());
    }

    @NotNull
    private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getExpressionInterceptUrlRegistryCustomizer() {
        return request -> {
            request
                    .antMatchers(
                            FORM_LOGIN_AUTHENTICATE_URL,
                            FORM_LOGIN_REQUEST_URL,
                            "/test/me"
                    ).permitAll()
                    .anyRequest().authenticated();
        };
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
                    一旦配置此路径，不再默认生成登录页面
                     */
//                    .loginPage(FORM_LOGIN_AUTHENTICATE_URL)
                    // 该路径即为表单登录的请求路径
                    .loginProcessingUrl(FORM_LOGIN_REQUEST_URL)
                    .usernameParameter(USERNAME_PARAMETER)
                    .passwordParameter(PASSWORD_PARAMETER)
                    .successHandler(jsonAuthenticationSuccessHandler)
                    .failureHandler(jsonAuthenticationFailureHandler);
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
//                .inMemoryAuthentication()
//                .withUser("hzw").password(bCryptPasswordEncoder.encode("123")).roles("USER").and().passwordEncoder(new BCryptPasswordEncoder());

                .jdbcAuthentication()
                .usersByUsernameQuery(
                        "select username,password, enabled from users where username=?")
                .authoritiesByUsernameQuery(
                        "select username, authority from authorities where username=?")
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * Expose the UserDetailsService as a Bean
     */
    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() {
        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
        userDetailsService.setDataSource(dataSource);
        return userDetailsService;
    }

    @Bean
    public SimpleRequestAuthenticationFilter simpleRequestAuthenticationFilter() throws Exception {
        SimpleRequestAuthenticationFilter simpleRequestAuthenticationFilter = new SimpleRequestAuthenticationFilter(objectMapper);
        simpleRequestAuthenticationFilter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler);
        simpleRequestAuthenticationFilter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler);
        simpleRequestAuthenticationFilter.setAuthenticationManager(authenticationManager());
        simpleRequestAuthenticationFilter.setFilterProcessesUrl("/authenticate/login");
        return simpleRequestAuthenticationFilter;
    }
}
