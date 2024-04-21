package com.example.CampusPartnerBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.CampusPartnerBackend.common.BaseResponse;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.common.ResultUtils;
import com.example.CampusPartnerBackend.constant.UserConstant;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.request.UserLoginRequest;
import com.example.CampusPartnerBackend.modal.request.UserRegisterRequest;
import com.example.CampusPartnerBackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/user")
//@CrossOrigin(originPatterns = {"http://localhost:3000"}, allowCredentials = "true", allowedHeaders = {"*"})
//@CrossOrigin(originPatterns = {"http://user.zhezi.online"}, allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userCode = userRegisterRequest.getUserCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword, userCode);
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登出")
    @PostMapping("/loginout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);

    }

    /**
     * 查询用户
     *
     * @param username
     * @param request
     * @return
     */
    @ApiOperation(value = "查询用户")
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (id < 0 || !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 根据标签查询用户
     */
    @ApiOperation(value = "根据标签查询用户")
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersBytags(tags);
        return ResultUtils.success(userList);
    }

    /**
     * 获取当前登录的用户信息
     */
    @ApiOperation(value = "获取当前登录的用户信息")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object useObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) useObj;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        User currentUser = userService.getUserById(userId);
        return ResultUtils.success(currentUser);
    }

    /**
     * 更新用户信息
     *
     * @param user    要修改的用户
     * @param request 请求参数
     * @return
     */
    @ApiOperation(value = "更新用户信息")
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }
    @ApiOperation(value = "推荐用户")
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUser(@RequestParam int pageSize, int pageNum, HttpServletRequest request) {
        Page<User> page = userService.selectByRedis(pageNum, pageSize, request);
        return ResultUtils.success(page);
    }

    /**
     * 根据标签匹配程度给用户推荐信息
     *
     * @param num     展示多少用户
     * @param request
     * @return
     */
    @ApiOperation(value = "根据标签匹配程度给用户推荐信息")
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userList = userService.matchUsers(num, loginUser);
        return ResultUtils.success(userList);
    }


}
