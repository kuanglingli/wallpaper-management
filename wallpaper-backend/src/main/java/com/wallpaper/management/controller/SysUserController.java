package com.wallpaper.management.controller;

import com.wallpaper.management.common.Result;
import com.wallpaper.management.entity.SysUser;
import com.wallpaper.management.service.SysUserService;
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
} 