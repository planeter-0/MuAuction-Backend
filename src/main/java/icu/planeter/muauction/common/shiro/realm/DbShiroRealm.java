package icu.planeter.muauction.common.shiro.realm;

import com.planeter.w2auction.common.shiro.matcher.BcryptCredentialsMatcher;
import com.planeter.w2auction.entity.*;
import com.planeter.w2auction.service.PermissionService;
import com.planeter.w2auction.service.UserService;
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
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();//UsernameAndPasswordToken
        // 从数据库中查找user信息
        User user = userService.findByUsername(username);
        if (null == user)
            throw new AuthenticationException("用户不存在");
        //认证成功,返回认证info
        return new SimpleAuthenticationInfo(
                user, //principal
                user.getPassword(), //hashedCredential
                ByteSource.Util.bytes(username), //salt
                getName() // realmName
        );
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();//主体
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //授予角色
        for (Role role : user.getRoles()) {
            simpleAuthorizationInfo.addRole(role.getName());
        }
        //授予权限
        simpleAuthorizationInfo.addStringPermissions(permissionService.getPermissionsByUsername(user));
        return simpleAuthorizationInfo;
    }
}
