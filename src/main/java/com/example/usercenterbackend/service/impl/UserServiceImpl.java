package com.example.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenterbackend.common.ErrorCode;
import com.example.usercenterbackend.exception.BusinessException;
import com.example.usercenterbackend.service.UserService;
import com.example.usercenterbackend.modal.domain.User;
import com.example.usercenterbackend.Mapper.UserMapper;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 13425
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-06 19:21:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    final String SALT = "yupi";


    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword,String userCode) {
        //1.账户，密码，校验码为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,userCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号，密码不能为空");
        }
        // 2.账户小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户过短");
        }
        // 3.密码，校验码小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //星球编号 <= 5位
        if(userCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }
        // 4.账户包含特殊字符（正则表达式）
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 5.密码和校验码不同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不同");
        }
        // 6.账户重复,放在后面，可以节省查询次数，节省内存性能
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userCode", userCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }
        //校验完成后，加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //注册成功，插入数据到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserCode(userCode);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.账户，密码，校验码为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号，密码不能为空");
        }
        // 2.账户小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户过短");
        }
        // 3.密码，校验码小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        // 4.账户包含特殊字符（正则表达式）
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //校验完成后，加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 6.账户重复,放在后面，可以节省查询次数，节省内存性能
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", userPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("userPassword can not match userAccount");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        //用户信息脱敏
        User safetyUser = getSafetyUser(user);
        //将用户信息保存到session中
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public User getSafetyUser(User user) {
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserCode(user.getUserCode());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




