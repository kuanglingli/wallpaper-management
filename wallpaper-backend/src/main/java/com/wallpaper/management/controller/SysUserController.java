package com.wallpaper.management.controller;

import com.wallpaper.management.common.Result;
import com.wallpaper.management.common.ResultCode;
import com.wallpaper.management.entity.SysUser;
import com.wallpaper.management.service.SysUserService;
import com.wallpaper.management.utils.JwtUtil;
import com.wallpaper.management.vo.LoginResultVO;
import com.wallpaper.management.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import java.util.Map;
import java.util.HashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 用户登录
     *
     * @param loginVO 登录信息
     * @return 结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResultVO> login(@RequestBody @Valid LoginVO loginVO) {
        LoginResultVO loginResult = sysUserService.login(loginVO.getUsername(), loginVO.getPassword());
        return Result.success(loginResult, "登录成功");
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Boolean> register(@Parameter(description = "用户信息", required = true) @RequestBody @Valid SysUser user) {
        boolean result = sysUserService.register(user);
        return Result.success(result, "注册成功");
    }

    /**
     * 获取当前用户信息
     *
     * @return 结果
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    @RequiresAuthentication
    public Result<SysUser> info() {
        SysUser user = sysUserService.getCurrentUser();
        // 安全起见，将密码置空
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 结果
     */
    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    @RequiresAuthentication
    public Result<Boolean> updatePassword(
            @Parameter(description = "旧密码", required = true) @RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
            @Parameter(description = "新密码", required = true) @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        boolean result = sysUserService.updateCurrentUserPassword(oldPassword, newPassword);
        return Result.success(result, "密码修改成功");
    }

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Operation(summary = "修改用户信息")
    @PutMapping
    @RequiresAuthentication
    public Result<Boolean> update(@Parameter(description = "用户信息", required = true) @RequestBody @Valid SysUser user) {
        // 不允许修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        boolean result = sysUserService.updateById(user);
        return Result.success(result, "信息修改成功");
    }

    /**
     * 禁用/启用用户
     *
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-正常）
     * @return 结果
     */
    @Operation(summary = "禁用/启用用户")
    @PutMapping("/status")
    @RequiresPermissions("user:manage")
    public Result<Boolean> updateStatus(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "状态（0-禁用，1-正常）", required = true) @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        boolean result = sysUserService.updateById(user);
        return Result.success(result, "状态更新成功");
    }

    /**
     * 用户登出
     *
     * @return 结果
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    @RequiresAuthentication
    public Result<Boolean> logout() {
        sysUserService.logout();
        return Result.success(true, "登出成功");
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌")
    public Result<Map<String, String>> refreshToken(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");
        
        if (org.springframework.util.StringUtils.hasText(refreshToken)) {
            try {
                // 验证刷新令牌
                DecodedJWT jwt = JwtUtil.parseToken(refreshToken);
                if (jwt == null) {
                    return Result.error(ResultCode.TOKEN_INVALID, "无效的刷新令牌");
                }
                
                // 获取用户名
                String username = JwtUtil.getUsername(refreshToken);
                if (username == null) {
                    return Result.error(ResultCode.TOKEN_INVALID, "无效的刷新令牌");
                }
                
                // 查询用户信息
                SysUser user = sysUserService.getByUsername(username);
                if (user == null) {
                    return Result.error(ResultCode.USER_NOT_EXIST, "用户不存在");
                }
                
                // 生成新的访问令牌和刷新令牌
                String newToken = JwtUtil.generateToken(user.getId(), username);
                String newRefreshToken = JwtUtil.generateToken(user.getId(), username); // 使用相同的方法生成刷新令牌
                
                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("token", newToken);
                tokenMap.put("refreshToken", newRefreshToken);
                
                return Result.success(tokenMap);
            } catch (TokenExpiredException e) {
                return Result.error(ResultCode.TOKEN_EXPIRED, "刷新令牌已过期，请重新登录");
            } catch (JWTVerificationException e) {
                return Result.error(ResultCode.TOKEN_INVALID, "无效的刷新令牌");
            } catch (Exception e) {
                return Result.error(ResultCode.SYSTEM_ERROR, "刷新令牌失败");
            }
        } else {
            return Result.error(ResultCode.PARAM_ERROR, "刷新令牌不能为空");
        }
    }
} 