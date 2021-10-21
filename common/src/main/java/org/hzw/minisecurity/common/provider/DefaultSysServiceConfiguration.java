package org.hzw.minisecurity.common.provider;

import org.hzw.minisecurity.common.mapper.*;
import org.hzw.minisecurity.common.service.*;
import org.hzw.minisecurity.common.service.impl.*;
import org.hzw.minisecurity.common.util.redis.RedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * 默认服务提供集合的配置类
 * @author hzw
 * @version 1.0
 * @date 2021/10/20 20:24
 */
@Configuration
public class DefaultSysServiceConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public ISysLoginService defaultSysLoginService(TokenService tokenService, AuthenticationManager authenticationManager, RedisCache redisCache) {
        return new DefaultSysLoginServiceImpl(tokenService, authenticationManager, redisCache);
    }
    @Bean
    @ConditionalOnMissingBean
    public IDefaultSysUserService defaultSysUserService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper) {
        return new DefaultSysUserServiceImpl(sysUserMapper, sysRoleMapper, sysUserRoleMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public IDefaultSysRoleService defaultSysRoleService(SysRoleMapper sysRoleMapper, SysRoleMenuMapper sysRoleMenuMapper, SysUserRoleMapper sysUserRoleMapper, SysRoleDeptMapper sysRoleDeptMapper) {
        return new DefaultSysRoleServiceImpl(sysRoleMapper, sysRoleMenuMapper, sysUserRoleMapper, sysRoleDeptMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public IDefaultSysMenuService defaultSysMenuService(SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper, SysRoleMenuMapper sysRoleMenuMapper) {
        return new DefaultSysMenuServiceImpl(sysMenuMapper, sysRoleMapper, sysRoleMenuMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public IDefaultSysDeptService defaultSysDeptService(SysDeptMapper sysMenuMapper, SysRoleMapper sysRoleMapper) {
        return new DefaultSysDeptServiceImpl(sysMenuMapper, sysRoleMapper);
    }
}
