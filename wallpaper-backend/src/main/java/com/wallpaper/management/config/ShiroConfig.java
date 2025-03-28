package com.wallpaper.management.config;

import com.wallpaper.management.shiro.JwtFilter;
import com.wallpaper.management.shiro.UserRealm;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置Shiro过滤器
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 添加自定义过滤器
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        // 设置拦截规则
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 登录、注册、Swagger、静态资源不需要认证
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs/**", "anon");
        filterChainDefinitionMap.put("/file/**", "anon");
        // 其他所有请求需要JWT认证
        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 配置Authenticator
     *
     * @return ModularRealmAuthenticator
     */
    @Bean
    public ModularRealmAuthenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return authenticator;
    }

    /**
     * 配置Authorizer
     *
     * @return ModularRealmAuthorizer
     */
    @Bean
    public ModularRealmAuthorizer authorizer() {
        return new ModularRealmAuthorizer();
    }

    /**
     * 配置会话管理器
     *
     * @return SessionManager
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 禁用会话Cookie
        sessionManager.setSessionIdCookieEnabled(false);
        // 禁用URL重写(URL中不会出现jsessionid参数)
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 配置安全管理器
     *
     * @param userRealm 用户Realm
     * @param authenticator 认证器
     * @param authorizer 授权器
     * @param sessionManager 会话管理器
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(UserRealm userRealm, 
                                          ModularRealmAuthenticator authenticator, 
                                          ModularRealmAuthorizer authorizer,
                                          SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setAuthenticator(authenticator);
        securityManager.setAuthorizer(authorizer);
        securityManager.setSessionManager(sessionManager);

        // 禁用Session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    /**
     * 配置用户Realm
     *
     * @return UserRealm
     */
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    /**
     * Shiro生命周期处理器
     *
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解
     *
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 开启Shiro的注解通知器
     *
     * @param securityManager 安全管理器
     * @return AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
} 