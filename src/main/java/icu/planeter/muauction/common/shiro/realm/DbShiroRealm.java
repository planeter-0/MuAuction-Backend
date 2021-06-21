package icu.planeter.muauction.common.shiro.realm;


import icu.planeter.muauction.common.shiro.matcher.BcryptCredentialsMatcher;
import icu.planeter.muauction.entity.Role;
import icu.planeter.muauction.entity.User;
import icu.planeter.muauction.service.PermissionService;
import icu.planeter.muauction.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:33
 * @status dev
 */
public class DbShiroRealm extends AuthorizingRealm {

    @Resource
    UserService userService;
    @Resource
    PermissionService permissionService;

    //设置凭证匹配器Bcrypt
    public DbShiroRealm() {
        this.setCredentialsMatcher(new BcryptCredentialsMatcher());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * Authentication
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String email = (String) authenticationToken.getPrincipal();//UsernameAndPasswordToken
        // find user from database
        User user = userService.findByEmail(email);
        if (null == user)
            throw new AuthenticationException("User does not exist");
        return new SimpleAuthenticationInfo(
                user, //principal
                user.getPassword(), //hashedCredential
                ByteSource.Util.bytes(email), //salt
                getName() // realmName
        );
    }

    /**
     * Authorization
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Role role : user.getRoles()) {
            simpleAuthorizationInfo.addRole(role.getName());
        }
        simpleAuthorizationInfo.addStringPermissions(permissionService.getPermissionsByUsername(user));
        return simpleAuthorizationInfo;
    }
}
