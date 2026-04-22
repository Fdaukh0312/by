package com.kedi.shop.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kedi.shop.entity.User;
import com.kedi.shop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 应用启动时初始化数据
 * 确保管理员账号存在
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", "admin");
        User existingAdmin = userMapper.selectOne(wrapper);

        if (existingAdmin == null) {
            // 插入管理员账号
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encodedPassword);
            admin.setNickname("管理员");
            admin.setRole(0);
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userMapper.insert(admin);
            System.out.println("============================================");
            System.out.println("  管理员账号已创建: admin / admin123");
            System.out.println("============================================");
        } else {
            // 确保密码是最新的 BCrypt 哈希
            existingAdmin.setPassword(encodedPassword);
            existingAdmin.setRole(0);
            existingAdmin.setStatus(1);
            existingAdmin.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(existingAdmin);
            System.out.println("============================================");
            System.out.println("  管理员账号密码已同步: admin / admin123");
            System.out.println("============================================");
        }
    }
}
