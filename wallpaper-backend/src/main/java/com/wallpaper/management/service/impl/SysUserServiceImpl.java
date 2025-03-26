package com.wallpaper.management.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.common.ResultCode;
import com.wallpaper.management.entity.SysUser;
import com.wallpaper.management.exception.BusinessException;
import com.wallpaper.management.mapper.SysUserMapper;
import com.wallpaper.management.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire}")
    private long jwtExpire;

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getStatus, 1));
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    @Override
    public String login(String username, String password) {
        // 查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 校验密码
        String encryptedPassword = encryptPassword(password);
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 生成JWT令牌
        return generateToken(user);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 是否成功
     */
    @Override
    public boolean register(SysUser user) {
        // 检查用户名是否已存在
        SysUser existingUser = getByUsername(user.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_EXIST);
        }

        // 加密密码
        user.setPassword(encryptPassword(user.getPassword()));
        user.setStatus(1);

        return save(user);
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 校验旧密码
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "旧密码不正确");
        }

        // 更新密码
        user.setPassword(encryptPassword(newPassword));

        return updateById(user);
    }

    /**
     * 生成JWT令牌
     *
     * @param user 用户信息
     * @return JWT令牌
     */
    private String generateToken(SysUser user) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtExpire * 1000);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("username", user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    /**
     * 密码加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        return new Md5Hash(password).toHex();
    }
} 