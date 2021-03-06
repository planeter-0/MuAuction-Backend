package icu.planeter.muauction.config;


import icu.planeter.muauction.common.CorsFilter;
import icu.planeter.muauction.common.JwtAuthFilter;
import icu.planeter.muauction.common.XSSFilter;
import icu.planeter.muauction.common.shiro.realm.DbShiroRealm;
import icu.planeter.muauction.common.shiro.realm.JwtShiroRealm;
import icu.planeter.muauction.service.UserService;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Map;

/**
 * @description: ShiroConfig
 * @author Planeter
 * @date 2021/5/15 0:43
 * @status dev
 */
@Configuration
public class ShiroConfig {

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(SecurityManager securityManager, UserService userService) throws Exception {
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter((Filter) shiroFilter(securityManager, userService).getObject());
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setAsyncSupported(true);
        filterRegistration.setEnabled(true);
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);

        return filterRegistration;
    }

    /**
     * ???realm, ???????????????????????????
     */
    @Bean
    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setRealms(Arrays.asList(jwtShiroRealm(), dbShiroRealm()));
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

//    /**
//     * session
//     */
//    @Bean
//    protected SessionStorageEvaluator sessionStorageEvaluator() {
//        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
//        sessionStorageEvaluator.setSessionStorageEnabled(false);
//        return sessionStorageEvaluator;
//    }

    @Bean("dbRealm")
    public Realm dbShiroRealm() {
        return new DbShiroRealm();
    }

    @Bean("jwtRealm")
    public Realm jwtShiroRealm() {
        return new JwtShiroRealm();
    }

    /**
     * ????????????????????????
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, UserService userService) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = factoryBean.getFilters();
        filterMap.put("jwt", new JwtAuthFilter());
        filterMap.put("xss",new XSSFilter());
        filterMap.put("cors",new CorsFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());
        return factoryBean;
    }

    /**
     * url????????????,??????authenticate
     * ?????????????????????????????????authorize
     */
    @Bean
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition c = new DefaultShiroFilterChainDefinition();
//        //No certification required
//        c.addPathDefinition("/tourist/**","anon,cors");
//        c.addPathDefinition("/register", "anon,xss,cors");
//        c.addPathDefinition("/login", "anon,xss,cors");
//        c.addPathDefinition("/logout", "anon,xss,cors");
//        c.addPathDefinition("/item/**","anon,xss,cors");
//        c.addPathDefinition("/searchItem","anon,cors");
//        c.addPathDefinition("/image/**","anon,cors");
//        //Jwt
        //Close Jwt in order to reduce the front-end work :)
        c.addPathDefinition("/**","anon,cors");
        return c;
    }
    /**
     * setUsePrefix(false)???????????????????????????bug????????????spring aop???????????????
     * ???@Controller??????????????????????????????@RequiresRole???shiro????????????????????????????????????????????????
     * ????????????404????????????????????????????????????bug
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

}
