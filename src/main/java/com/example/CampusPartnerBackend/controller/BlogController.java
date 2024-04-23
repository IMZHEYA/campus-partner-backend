package com.example.CampusPartnerBackend.controller;

import com.example.CampusPartnerBackend.common.BaseResponse;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.common.ResultUtils;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.Blog;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.request.blog.BlogAddRequest;
import com.example.CampusPartnerBackend.service.BlogService;
import com.example.CampusPartnerBackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "博客相关接口")
@RestController
@RequestMapping("/blog")
//@CrossOrigin(originPatterns = {"http://user.zhezi.online"}, allowCredentials = "true")
@Slf4j
public class BlogController {

    @Resource
    private BlogService blogService;

    @Resource
    private UserService userService;

//    @Resource
//    private UserBlogService userBlogService;

    @ApiOperation(value = "添加博客")
    @PostMapping("/add")
    public BaseResponse<Long> addBlog(@RequestBody BlogAddRequest blogAddRequest, HttpServletRequest request) {
        if (blogAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogAddRequest,blog);
        User loginUser = userService.getLoginUser(request);
        Long blogId = blogService.addBlog(blog, loginUser);
        return ResultUtils.success(blogId);
    }
//    @ApiOperation(value = "删除队伍")
//    @PostMapping("/delete")
//    public BaseResponse<Boolean> deleteBlog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
//        if (deleteRequest == null || deleteRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        boolean result = blogService.deleteBlog(deleteRequest.getId(),loginUser);
//        if (!result) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
//        }
//        return ResultUtils.success(true);
//    }
//    @ApiOperation(value = "更新队伍")
//    @PostMapping("/update")
//    public BaseResponse<Boolean> updateBlog(@RequestBody BlogUpdateRequest blogUpdateRequest,HttpServletRequest request) {
//        if (blogUpdateRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        boolean result = blogService.updateBlog(blogUpdateRequest,loginUser);
//        if (!result) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
//        }
//        return ResultUtils.success(true);
//    }
//    @ApiOperation(value = "根据获取脱敏队伍信息")
//    @GetMapping("/get")
//    public BaseResponse<Blog> getBlogById(Long id) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Blog blog = blogService.getById(id);
//        if (blog == null) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        return ResultUtils.success(blog);
//    }
//
//    /**
//     * 前端传入一些信息，可以查询到与这些信息相匹配的队伍信息
//     * @param blogQuery
//     * @return
//     */
//    @ApiOperation(value = "查询队伍")
//    @GetMapping("/list")
//    public BaseResponse<List<BlogUserVO>> listBlogs(BlogQuery blogQuery,HttpServletRequest request) {
//        if (blogQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean isAdmin = userService.isAdmin(request);
//        //1.先查出队伍id列表
//        List<BlogUserVO> blogList = blogService.listBlogs(blogQuery,isAdmin);
//        List<Long> blogIdList = blogList.stream().map(BlogUserVO::getId).collect(Collectors.toList());
//        //2.判断队伍是否已加入队伍,未登录捕获异常
//        try {
//            User loginUser = userService.getLoginUser(request);
//            QueryWrapper<UserBlog> userBlogQueryWrapper = new QueryWrapper<>();
//            userBlogQueryWrapper.eq("userId",loginUser.getId());
//            userBlogQueryWrapper.in("blogId",blogIdList);
//            List<UserBlog> userBlogList = userBlogService.list(userBlogQueryWrapper);
//            //已加入的队伍id集合
//            Set<Long> hasJoinBlogSet = userBlogList.stream().map(UserBlog::getBlogId).collect(Collectors.toSet());
//            //遍历队伍列表，设置是否加入属性
//            blogList.forEach(blog -> {
//                boolean hasJoin = hasJoinBlogSet.contains(blog.getId());
//                blog.setHasJoin(hasJoin);
//            });
//
//        } catch (Exception e) {}
//        //3，队伍已加入用户数
//        QueryWrapper<UserBlog> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("blogId",blogIdList);
//        List<UserBlog> userBlogList = userBlogService.list(queryWrapper);
//        Map<Long, List<UserBlog>> blogIdUserBlogList  = userBlogList.stream().collect(Collectors.groupingBy(UserBlog::getBlogId));
//        blogList.forEach(blog -> {
//            blog.setHasJoinNum(blogIdUserBlogList.getOrDefault(blog.getId(),new ArrayList<>()).size());
//        });
//        return ResultUtils.success(blogList);
//    }
//
//    @ApiOperation(value = "分页查询队伍")
//    @GetMapping("/list/page")
//    public BaseResponse<Page<Blog>> listBlogsByPage(BlogQuery blogQuery){
//        if(blogQuery == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Blog blog = new Blog();
//        BeanUtils.copyProperties(blogQuery,blog);
//        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>(blog);
//        int current = blogQuery.getPageNum();
//        int pageSize = blogQuery.getPageSize();
//        Page<Blog> page = new Page<>(current,pageSize);
//        Page<Blog> pageBlogs = blogService.page(page, blogQueryWrapper);
//        return ResultUtils.success(pageBlogs);
//    }
//    @ApiOperation(value = "加入队伍")
//    @PostMapping("/join")
//    public BaseResponse<Boolean> joinBlog(@RequestBody BlogJoinRequest blogJoinRequest,HttpServletRequest request){
//        if(blogJoinRequest == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUSer = userService.getLoginUser(request);
//        boolean result = blogService.joinBlog(blogJoinRequest,loginUSer);
//        return ResultUtils.success(result);
//    }
//    @ApiOperation(value = "退出队伍")
//    @PostMapping("/quit")
//    public BaseResponse<Boolean> quitBlog(@RequestBody BlogQuitRequest blogQuitRequest,HttpServletRequest request){
//        if(blogQuitRequest == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        boolean result =  blogService.quitBlog(blogQuitRequest,loginUser);
//        return ResultUtils.success(result);
//    }
//
//    /**
//     * 获取当前用户已创建的队伍
//     * @param blogQuery
//     * @param request
//     * @return
//     */
//    @ApiOperation(value = "获取当前用户已创建的队伍")
//    @GetMapping("/list/my/create")
//    public BaseResponse<List<BlogUserVO>> listMyCreateBlogs(BlogQuery blogQuery,HttpServletRequest request) {
//        if (blogQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        blogQuery.setUserId(loginUser.getId());
//        List<BlogUserVO> blogList = blogService.listBlogs(blogQuery,true);
//        return ResultUtils.success(blogList);
//    }
//    @ApiOperation(value = "获取当前用户已加入的队伍")
//    @GetMapping("/list/my/join")
//    public BaseResponse<List<BlogUserVO>> listMyJoinBlogs(BlogQuery blogQuery,HttpServletRequest request) {
//        if (blogQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        Long userId = loginUser.getId();
//        QueryWrapper<UserBlog> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userId",userId);
//        List<UserBlog> userBlogList = userBlogService.list(queryWrapper);
//        Map<Long, List<UserBlog>> listMap = userBlogList.stream().collect(Collectors.groupingBy(UserBlog::getBlogId));
//        ArrayList idlist = new ArrayList<>(listMap.keySet());
//        blogQuery.setIdList(idlist);
//        List<BlogUserVO> blogList = blogService.listBlogs(blogQuery,true);
//        return ResultUtils.success(blogList);
//    }
}
