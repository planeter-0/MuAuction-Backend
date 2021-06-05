package icu.planeter.muauction.common.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @description: BcryptCredentialsMatcher
 * @author Planeter
 * @date 2021/5/15 0:23
 * @status dev
 */
public class BcryptCredentialsMatcher  implements CredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        // Plaintext password to be verified
        String plaintext = new String(userToken.getPassword());
        // Encrypted password in database
        String hashed = info.getCredentials().toString();
        return BCrypt.checkpw(plaintext, hashed);
    }
}
