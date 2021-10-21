package org.hzw.minisecurity.common.service;

import org.hzw.minisecurity.common.constant.Constants;
import org.hzw.minisecurity.common.domain.model.LoginUser;
import org.hzw.minisecurity.common.exception.CustomException;
import org.hzw.minisecurity.common.exception.user.CaptchaException;
import org.hzw.minisecurity.common.exception.user.CaptchaExpireException;
import org.hzw.minisecurity.common.exception.user.UserPasswordNotMatchException;
import org.hzw.minisecurity.common.manager.AsyncManager;
import org.hzw.minisecurity.common.manager.factory.AsyncFactory;
import org.hzw.minisecurity.common.provider.TokenService;
import org.hzw.minisecurity.common.util.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 * 如想自定义登录逻辑，请实现此接口
 * 
 * @author ruoyi
 */
public interface ISysLoginService
{
    
    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid);

}
