package com.example.usercenterbackend.service;
import java.util.Date;

import com.example.usercenterbackend.modal.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void test(){
        User user = new User();
        user.setUsername("yupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://profile-avatar.csdnimg.cn/9751285f58b94575a8a50bad240cc012_m0_74870396.jpg!1");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("321");
        user.setUserCode("1");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);

    }

}