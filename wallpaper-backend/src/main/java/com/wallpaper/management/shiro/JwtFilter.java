package com.wallpaper.management.shiro;

import cn.hutool.core.util.StrUtil;
import com.wallpaper.management.common.Result;
import com.wallpaper.management.common.ResultCode;
import com.wallpaper.management.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT过滤器
 */
@Slf4j
public class JwtFilter extends AuthenticatingFilter {

    /**
     * 创建Token
     *
     * @param request  请求
     * @param response 响应
     * @return Token
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        // 获取请求头中的Token
        String token = getRequestToken((HttpServletRequest) request);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return new JwtToken(token);
    }

    /**
     * 判断是否允许访问
     *
     * @param request  请求
     * @param response 响应
     * @param mappedValue 映射值
     * @return 是否允许访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 对于OPTIONS请求直接放行
        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否拒绝访问（认证逻辑）
     *
     * @param request  请求
     * @param response 响应
     * @return 是否拒绝访问
     * @throws Exception 异常
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 获取请求头中的Token
        String token = getRequestToken((HttpServletRequest) request);
        if (StrUtil.isBlank(token)) {
            // 无Token，返回未授权
            responseError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 验证Token是否有效
        if (!JwtUtil.verify(token)) {
            // Token无效，返回未授权
            responseError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 执行登录
        return executeLogin(request, response);
    }

    /**
     * 登录失败处理
     *
     * @param token    Token
     * @param e        异常
     * @param request  请求
     * @param response 响应
     * @return 是否继续处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        // 登录失败，返回未授权
        responseError(response, ResultCode.UNAUTHORIZED);
        return false;
    }

    /**
     * 从请求头中获取Token
     *
     * @param request 请求
     * @return Token
     */
    private String getRequestToken(HttpServletRequest request) {
        // 从请求头中获取Token
        String token = request.getHeader("Authorization");
        log.info("从请求头中获取Authorization: {}", token);
        
        if (StrUtil.isNotBlank(token)) {
            // 如果Token以Bearer开头，则去掉Bearer
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                log.info("提取Bearer后的token: {}", token);
            }
            return token;
        }
        // 如果请求头中没有Token，则从请求参数中获取
        token = request.getParameter("token");
        log.info("从请求参数中获取token: {}", token);
        return token;
    }

    /**
     * 返回错误响应
     *
     * @param response   响应
     * @param resultCode 错误码
     */
    private void responseError(ServletResponse response, ResultCode resultCode) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setStatus(200);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(Result.error(resultCode));
            httpResponse.getWriter().print(json);
        } catch (IOException e) {
            log.error("返回错误响应失败", e);
        }
    }
} 