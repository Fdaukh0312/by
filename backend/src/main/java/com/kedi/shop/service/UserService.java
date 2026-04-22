package com.kedi.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kedi.shop.entity.*;
import com.kedi.shop.mapper.UserMapper;
import com.kedi.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(LoginDTO loginDTO) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDTO.getUsername());
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    public void register(RegisterDTO registerDTO) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", registerDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(1);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.save(user);
    }

    public User getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    public void updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
    }

    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        return this.updateById(user);
    }
}
