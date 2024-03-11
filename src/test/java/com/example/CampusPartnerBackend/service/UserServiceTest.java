package com.example.CampusPartnerBackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.CampusPartnerBackend.Mapper.UserMapper;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

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


    @Test
    public void searchUsersBytags() {
        List<String> tags = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersBytags(tags);
        Assertions.assertNotNull(userList);
    }


    @Test
    void test1(){
        //期望返回1但实际返回了多条
//        User user = userMapper.selectOne(null);
    }

}