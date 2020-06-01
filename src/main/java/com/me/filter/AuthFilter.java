package com.me.filter;

import com.me.entity.User;
import com.me.service.UserService;
import com.me.web.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

@Component
public class AuthFilter implements Filter {
    @Autowired
    UserService userService;

    /**
     * 当一个Filter作为Spring容器管理的Bean存在时，可以通过DelegatingFilterProxy间接地引用它并使其生效。
     *
     * eg:允许用户使用Basic模式进行用户验证，即在HTTP请求中添加头Authorization: Basic email:password
     * Basic认证模式并不安全，本节只用来作为使用Filter的示例
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // 获取Authorization头:
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // 从Header中提取email和password:
            String[] info = new String(Base64.getDecoder().decode(authHeader.substring(6))).split(":");
            System.out.println();
            if (info.length == 2) {
                String email = info[0];
                String password = info[1];

                // 登录:
                User user = userService.signin(email, password);
                // 放入Session:
                req.getSession().setAttribute(UserController.KEY_USER, user);
            }

        }
        // 继续处理请求:
        chain.doFilter(request, response);
    }
}