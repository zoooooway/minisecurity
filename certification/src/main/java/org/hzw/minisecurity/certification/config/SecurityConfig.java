package org.hzw.minisecurity.certification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzw.minisecurity.certification.filter.JwtAuthenticationTokenFilter;
import org.hzw.minisecurity.certification.filter.SimpleRequestAuthenticationFilter;
import org.hzw.minisecurity.certification.handler.FailureAuthenticationEntryPoint;
import org.hzw.minisecurity.certification.handler.JsonAuthenticationFailureHandler;
import org.hzw.minisecurity.certification.handler.JsonAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hzw
 * @version 1.0
 * @date 2021/9/23 19:49
 */
@ConditionalOnMissingBean(SecurityConfig.class)
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * ????????????????????????????????????
     */
    private static final String USERNAME_PARAMETER ="username";
    /**
     * ?????????????????????????????????
     */
    private static final String PASSWORD_PARAMETER ="password";

    /**
     * ??????????????????????????????
     */
    private static final String FORM_LOGIN_AUTHENTICATE_URL ="/login";
    /**
     * ??????????????????????????????
     */
    private static final String FORM_LOGIN_REQUEST_URL ="/formLogin";

    /**
     * ?????? ?????????????????? AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Autowired
    private JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;
    @Autowired
    private UserDetailsService userDetailsServiceImpl;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private FailureAuthenticationEntryPoint failureAuthenticationEntryPoint;

    private ObjectMapper objectMapper = new ObjectMapper();
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private List<String> permitPatterns;

    {
        permitPatterns = new ArrayList<>();
        permitPatterns.add(FORM_LOGIN_AUTHENTICATE_URL);
        permitPatterns.add(FORM_LOGIN_REQUEST_URL);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin(Customizer.withDefaults());
        http
                // ??????????????????
//                .formLogin(getFormLoginConfigurerCustomizer())
                // ?????????????????????????????????
                .authorizeRequests(getExpressionInterceptUrlRegistryCustomizer())
                //  ???????????????????????????
                .exceptionHandling( except -> except.authenticationEntryPoint(failureAuthenticationEntryPoint))
//                .addFilterAt(simpleRequestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // ??????session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf(csrf -> csrf.disable());
    }

    @NotNull
    private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getExpressionInterceptUrlRegistryCustomizer() {
        return request -> {
            request
                    .antMatchers(
                            this.permitPatterns.toArray(new String[0])
                    ).permitAll()
                    .anyRequest().authenticated();
        };
    }

    /**
     * configure those requests that are login by form
     * @return formLoginCustomizer???Customizer
     */
    @org.jetbrains.annotations.NotNull
    private Customizer<FormLoginConfigurer<HttpSecurity>> getFormLoginConfigurerCustomizer() {
        return form -> {
            form
                    /*
                    Specifies the URL to send users to if login is required.

                    ??????????????????????????????????????????????????? ????????????????????????????????????????????????????????????
                    ???????????????????????????
                    ??????????????????????????????????????????????????????
                     */
//                    .loginPage(FORM_LOGIN_AUTHENTICATE_URL)
                    // ??????????????????????????????????????????
                    .loginProcessingUrl(FORM_LOGIN_REQUEST_URL)
                    .usernameParameter(USERNAME_PARAMETER)
                    .passwordParameter(PASSWORD_PARAMETER)
//                    .successHandler(jsonAuthenticationSuccessHandler)
                    .failureHandler(jsonAuthenticationFailureHandler);
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(new BCryptPasswordEncoder());
    }


    protected SecurityConfig addPermitPattern(String pattern) {
        this.permitPatterns.add(pattern);
        return this;
    }
}
