package com.wallpaper.management.vo;

import com.wallpaper.management.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录结果")
public class LoginResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT令牌
     */
    @Schema(description = "JWT令牌")
    private String token;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private SysUser userInfo;
} 