package com.example.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenterbackend.service.UserService;
import com.example.usercenterbackend.modal.domain.User ;
import com.example.usercenterbackend.Mapper.UserMapper ;
import org.springframework.stereotype.Service;

/**
* @author 13425
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-06 19:21:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




