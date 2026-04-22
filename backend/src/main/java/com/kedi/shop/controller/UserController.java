package com.kedi.shop.controller;

import com.kedi.shop.entity.LoginDTO;
import com.kedi.shop.entity.RegisterDTO;
import com.kedi.shop.entity.User;
import com.kedi.shop.service.UserService;
import com.kedi.shop.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return Result.success("注册成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestAttribute Long userId, @RequestBody User user) {
        try {
            user.setId(userId);
            userService.updateUser(user);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestAttribute Long userId,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
