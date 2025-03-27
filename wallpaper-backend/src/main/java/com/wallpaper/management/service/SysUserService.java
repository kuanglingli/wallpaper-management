package com.wallpaper.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.SysUser;
import com.wallpaper.management.vo.LoginResultVO;

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
     * @return 登录结果
     */
    LoginResultVO login(String username, String password);

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
    
    /**
     * 获取当前登录用户
     *
     * @return 当前用户信息
     */
    SysUser getCurrentUser();
    
    /**
     * 修改当前用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updateCurrentUserPassword(String oldPassword, String newPassword);
    
    /**
     * 用户登出
     */
    void logout();
} 