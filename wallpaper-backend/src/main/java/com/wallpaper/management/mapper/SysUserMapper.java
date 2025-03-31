package com.wallpaper.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wallpaper.management.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    Integer isAdminByUserId(Long userId);
} 