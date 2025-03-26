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
import com.wallpaper.management.utils.JwtUtil;
import com.wallpaper.management.utils.ShiroUtil;
import com.wallpaper.management.vo.LoginResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire}")
    private long jwtExpire;

    /**
     * 盐值
     */
    private static final String SALT = "wallpaper";

    /**
     * 加密算法
     */
    private static final String ALGORITHM_NAME = "md5";

    /**
     * 加密次数
     */
    private static final int HASH_ITERATIONS = 2;

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
     * @return 登录结果
     */
    @Override
    public LoginResultVO login(String username, String password) {
        log.info("用户登录 - username: {}", username);
        
        // 根据用户名查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new BusinessException("用户不存在");
        }

        // 校验用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已被禁用: {}", username);
            throw new BusinessException("用户已被禁用");
        }

        // 校验密码
        String encryptedPassword = encryptPassword(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            log.warn("密码错误 - username: {}", username);
            throw new BusinessException("密码错误");
        }

        // 生成token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("生成token成功 - username: {}, tokenLength: {}", username, token != null ? token.length() : 0);
        
        if (token == null || token.isEmpty()) {
            log.error("生成token失败，为空值 - username: {}", username);
            throw new BusinessException("生成token失败");
        }

        // 返回登录结果
        LoginResultVO loginResult = new LoginResultVO();
        loginResult.setToken(token);
        // 安全起见，将密码置空
        user.setPassword(null);
        loginResult.setUserInfo(user);
        
        log.info("用户登录成功 - username: {}, token: {}", username, token);
        return loginResult;
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 是否成功
     */
    @Override
    public boolean register(SysUser user) {
        // 校验用户名是否已存在
        SysUser existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 设置初始状态为正常
        user.setStatus(1);
        
        // 加密密码
        user.setPassword(encryptPassword(user.getPassword()));
        
        // 保存用户
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
     * 获取当前登录用户
     *
     * @return 当前用户信息
     */
    @Override
    public SysUser getCurrentUser() {
        Long userId = ShiroUtil.getUserId();
        if (userId == null) {
            return null;
        }
        return getById(userId);
    }
    
    /**
     * 修改当前用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public boolean updateCurrentUserPassword(String oldPassword, String newPassword) {
        // 获取当前用户
        SysUser user = getCurrentUser();
        if (user == null) {
            throw new BusinessException("用户未登录");
        }
        
        // 校验旧密码
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setPassword(encryptPassword(newPassword));
        
        return updateById(updateUser);
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
        return new SimpleHash(ALGORITHM_NAME, password, SALT, HASH_ITERATIONS).toHex();
    }
} 