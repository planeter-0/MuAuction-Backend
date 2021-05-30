package icu.planeter.muauction.common.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;
/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:31
 * @status dev
 */
public class Jwt implements HostAuthenticationToken {
    private String token;
    private String host;

    public Jwt(String token) {
        this(token, null);
    }

    public Jwt(String token, String host) {
        this.token = token;
        this.host = host;
    }

    public String getToken() {
        return token;
    }

    public String getHost() {
        return host;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public String toString() {
        return token + ':' + host;
    }
}
