package org.hzw.minisecurity.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.hzw.minisecurity.common.constant.Constants;
import org.hzw.minisecurity.common.exception.CustomException;
import org.hzw.minisecurity.common.exception.user.CaptchaException;
import org.hzw.minisecurity.common.exception.user.CaptchaExpireException;
import org.hzw.minisecurity.common.exception.user.UserPasswordNotMatchException;
import org.hzw.minisecurity.common.manager.AsyncManager;
import org.hzw.minisecurity.common.manager.factory.AsyncFactory;
import org.hzw.minisecurity.common.domain.model.LoginUser;
import org.hzw.minisecurity.common.provider.TokenService;
import org.hzw.minisecurity.common.service.ISysLoginService;
import org.hzw.minisecurity.common.util.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@RequiredArgsConstructor
public class DefaultSysLoginServiceImpl implements ISysLoginService
{
    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    private final RedisCache redisCache;

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    @Override
    public String login(String username, String password, String code, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "验证码过期"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "验证码错误"));
            throw new CaptchaException();
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名密码不匹配"));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功"));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
