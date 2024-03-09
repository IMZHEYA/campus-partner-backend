package com.example.usercenterbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usercenterbackend.modal.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 13425
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-03-06 19:21:19
 */
public interface UserService extends IService<User> {
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User user);

    int userLogout(HttpServletRequest request);
}
