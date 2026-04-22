package com.kedi.shop.config;

import com.kedi.shop.util.JwtUtil;
import com.kedi.shop.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            sendUnauthorized(response, "未登录");
            return false;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorized(response, "Token无效");
            return false;
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);
        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(Result.error(401, message)));
    }
}
