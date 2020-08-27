package com.starnet.ipcmonitorcloud.web.config;

import com.starnet.ipcmonitorcloud.exception.AuthException;
import com.starnet.ipcmonitorcloud.web.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * AuthInterceptor
 *
 * @author wzz
 * @date 2020/8/25 11:28
 **/
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private UserAccountService userAccountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
//        log.info("{}, {}", request.getRequestURI(), method.getName());
        if (method.isAnnotationPresent(NoToken.class)) {
            return method.getAnnotation(NoToken.class).required();
        } else {
            String token = request.getHeader("Authorization");
            boolean flag = userAccountService.validateLoginToken(token);
            if (!flag) {
                throw new AuthException("Need a right token to access");
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
