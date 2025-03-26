package com.wallpaper.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.SysUser;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    String login(String username, String password);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(SysUser user);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
} 