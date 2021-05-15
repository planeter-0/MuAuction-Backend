package icu.planeter.muauction.common.filter;

import com.planeter.w2auction.common.shiro.JWTToken;
import com.planeter.w2auction.common.utils.JwtUtils;
import com.planeter.w2auction.entity.User;
import com.planeter.w2auction.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:43
 * @status dev
 */
@Slf4j
@Component
public class JWTAuthFilter extends AuthenticatingFilter {
    @Resource
    UserService userService;
    // token更新时间.单位秒
    private static final int tokenRefreshInterval = 3000;

    private static JwtAuthFilter jwtAuthFilter;

    @PostConstruct
    public void init() {
        jwtAuthFilter = this;
    }

    /**
     * 父第一个被调用的方法
     * 返回true则继续，返回false则会调用onAccessDenied()。
     */
    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginRequest(request, response))
            return true; // 不拦截登录请求
        boolean allowed = false;
        try {
        allowed = executeLogin(request, response); //处理登录
        } catch (IllegalStateException e) { // not found any token
            log.warn("Invalid token");
        } catch (Exception e) {
            log.error("Error occurs when login", e);
        }
        return allowed;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        //从header获取token value
        HttpServletRequest httpRequest = WebUtils.toHttp(servletRequest);
        String header = httpRequest.getHeader("x-auth-token");
//        String jwtToken = StringUtils.removeStart(header, "Bearer ");
        // 非空且不过期,返回原token
        if (StringUtils.isNotBlank(header) && !JwtUtils.isTokenExpired(header))
            return new JWTToken(header);
        return null;//进入isAccessAllowed（）的异常处理逻辑
    }

    /**
     * isAccessAllowed()返回false,返回错误响应
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
        return false;
    }

    /**
     * 认证成功
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JWTToken) {
            JWTToken jwtToken = (JWTToken) token;
            User user = (User) subject.getPrincipal();
            //检查token过期,若过期更新盐生成新token,因为盐的更新,过期token失效
            Date date = JwtUtils.getIssuedAt(jwtToken.getToken());
            assert date != null;
            if (shouldTokenRefresh(date)) {
                newToken = jwtAuthFilter.userService.generateJwtToken(user.getUsername());
            }
        }
        // 新token被加入响应头
        if (StringUtils.isNotBlank(newToken))
            httpResponse.setHeader("x-auth-token", newToken);
        response = WebUtils.getResponse(httpResponse);
        return true;
    }

    /**
     * 认证失败，回调
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("Validate token fail, token:{}, error:{}", token.toString(), e.getMessage());
        return false;
    }

    /**
     * 比较 jwt生成时间 是否 晚于 (现在时间 - jwt有效时间 )
     */
    protected boolean shouldTokenRefresh(Date issueAt) {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(tokenRefreshInterval).isAfter(issueTime);
    }

}
