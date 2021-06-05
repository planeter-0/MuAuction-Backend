package icu.planeter.muauction.common.shiro.realm;

import icu.planeter.muauction.common.shiro.Jwt;
import icu.planeter.muauction.common.shiro.matcher.JWTCredentialsMatcher;
import icu.planeter.muauction.common.utils.JwtUtils;
import icu.planeter.muauction.dto.JwtUser;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:33
 * @status dev
 */
public class JwtShiroRealm extends AuthorizingRealm {
    @Resource
    UserService userService;
    // 设置Matcher
    public JwtShiroRealm(){
        this.setCredentialsMatcher(new JWTCredentialsMatcher());
    }
    /**
     * 设置支持的token
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof Jwt;
    }
    /**
     * JWTRealm只负责登陆后的请求认证
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }

    /**
     * 获取用户的salt值
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        Jwt jwtToken = (Jwt) authToken;
        String token = jwtToken.getToken();
        String email = JwtUtils.getUsername(token);
        User user = userService.findByEmail(email);
        String tokenInCache = userService.getJwt(email);
        if(user == null)
            throw new AuthenticationException("Token expired, please login again");
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //principal
                token, //credential
                getName()); // realmName
        return authenticationInfo;
    }
}
