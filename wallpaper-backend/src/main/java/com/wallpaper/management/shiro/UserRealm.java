package com.wallpaper.management.shiro;

import com.wallpaper.management.entity.SysUser;
import com.wallpaper.management.service.SysUserService;
import com.wallpaper.management.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户Realm
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 判断Token类型是否支持
     *
     * @param token Token
     * @return 是否支持
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权（权限验证）
     *
     * @param principals 身份
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String token = (String) principals.getPrimaryPrincipal();
        Long userId = JwtUtil.getUserId(token);
        
        // 获取用户角色和权限
        // TODO: 实现获取用户角色和权限的逻辑
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        
        // 这里简化处理，实际应该从数据库中获取用户的角色和权限
        if (userId != null && userId == 1) {
            // 管理员
            roles.add("ROLE_ADMIN");
            permissions.add("user:manage");
            permissions.add("role:manage");
            permissions.add("wallpaper:upload");
            permissions.add("wallpaper:edit");
            permissions.add("wallpaper:audit");
            permissions.add("wallpaper:download");
            permissions.add("comment:manage");
        } else {
            // 普通用户
            roles.add("ROLE_USER");
            permissions.add("wallpaper:upload");
            permissions.add("wallpaper:download");
        }
        
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        
        return info;
    }

    /**
     * 认证（登录验证）
     *
     * @param authenticationToken Token
     * @return 认证信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getPrincipal();

        // 校验Token
        if (!JwtUtil.verify(token)) {
            throw new AuthenticationException("Token已失效");
        }

        // 获取用户信息
        String username = JwtUtil.getUsername(token);
        Long userId = JwtUtil.getUserId(token);
        
        // 校验用户是否存在
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }
        
        // 校验用户状态
        if (user.getStatus() == 0) {
            throw new LockedAccountException("用户已被禁用");
        }
        
        // 校验用户ID是否匹配
        if (!userId.equals(user.getId())) {
            throw new AuthenticationException("Token与用户不匹配");
        }

        return new SimpleAuthenticationInfo(token, token, getName());
    }
} 