package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.constant.UserConstant;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.service.UserService;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.Mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.CampusPartnerBackend.constant.UserConstant.USER_LOGIN_STATE;

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
    public Long userRegister(String userAccount, String userPassword, String checkPassword, String userCode) {
        //1.账户，密码，校验码为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号，密码不能为空");
        }
        // 2.账户小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户过短");
        }
        // 3.密码，校验码小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        //星球编号 <= 5位
        if (userCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 4.账户包含特殊字符（正则表达式）
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 5.密码和校验码不同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不同");
        }
        // 6.账户重复,放在后面，可以节省查询次数，节省内存性能
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userCode", userCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已存在");
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
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.账户，密码，校验码为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号，密码不能为空");
        }
        // 2.账户小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户过短");
        }
        // 3.密码，校验码小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 4.账户包含特殊字符（正则表达式）
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
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
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUsersBytags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        //从数据库中进行模糊查询
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for(String tag : tags){
//            queryWrapper.like("tags",tag);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        //脱敏
//        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
//        for (User user : userList) {
        //内存中用户的标签
//            String Usertags = user.getTags();
//            Set<String> tagSet = gson.fromJson(Usertags, new TypeToken<Set<String>>() {
//            }.getType());
        //遍历传入的标签列表，如果有一个标签用户不存在，就不行，全部存在才可以
//            for (String tag : tags) {
//                if (!tagSet.contains(tag)) {
//                    return false;
//                }
//            }
//            return true;
//        }
        //语法糖
        return userList.stream().filter(user -> {
            String Usertags = user.getTags();
            if (StringUtils.isBlank(Usertags)) {
                return false;
            }
            Set<String> tagSet = gson.fromJson(Usertags, new TypeToken<Set<String>>() {
            }.getType());
            tagSet = Optional.ofNullable(tagSet).orElse(new HashSet<>());
            for (String tag : tags) {
                if (!tagSet.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(user -> getSafetyUser(user)).collect(Collectors.toList());
//        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        if(id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userMapper.selectById(id);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User safetyUser = getSafetyUser(user);
        return safetyUser;
    }

    /**
     * 是否是管理员
     */
    public boolean isAdmin(HttpServletRequest request) {
        Object userobj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userobj;
        if (user == null || user.getUserRole() != UserConstant.ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        if (loginUser == null || loginUser.getUserRole() != UserConstant.ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User safetyUser = getSafetyUser(user);
        return safetyUser;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User user1 = userMapper.selectById(userId);
        if(user1 == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int i = userMapper.updateById(user);
        return i;
    }
}




