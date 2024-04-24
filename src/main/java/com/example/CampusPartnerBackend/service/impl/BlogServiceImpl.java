package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.exception.ThrowUtils;
import com.example.CampusPartnerBackend.modal.domain.Blog;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.enums.UserRoleEnum;
import com.example.CampusPartnerBackend.modal.request.blog.BlogUpdateRequest;
import com.example.CampusPartnerBackend.service.BlogService;
import com.example.CampusPartnerBackend.mapper.BlogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author 13425
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2024-04-22 19:20:50
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService{
    /**
     * 添加博客
     * @param blog 添加博客
     * @param loginUser 登录用户
     * @return
     */
    @Override
    public Long addBlog(Blog blog, User loginUser) {
        //1.请求参数是否为空
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3.检验信息
        //(1)博客标题 <=20
        String name = blog.getTitle();
        if (name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客标题过长");
        }
        //(2)图片最多为9张，多张以"/"隔开
        String images = blog.getImages();
        String[] imagess = images.split("/");
        if(imagess.length > 9){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"图片数量过多");
        }
        // (3) 0 <= 博客内容 <= 512
        String content = blog.getContent();
        if (content.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客内容过长");
        }
        if(content.length() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"博客内容不能为空");
        }
        //8.插入队伍消息到队伍表
        blog.setId(null);
        blog.setUserId(loginUser.getId());
        boolean save = this.save(blog);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建博客失败");
        }
        return blog.getId();
    }

    /**
     * 删除博客
     * @param id 博客id
     * @param loginUser 当前登录用户
     * @return
     */
    @Override
    public boolean deleteBlog(Long id, User loginUser) {
        // 1. 校验请求参数（队伍id）
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 校验博客是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"博客不存在");
        }
        //3. 校验当前登录用户是管理员 或者 是博客的作者才可以删除博客
        Integer userRole = loginUser.getRole();
        Long userId = blog.getUserId();
        ThrowUtils.throwIf(!UserRoleEnum.ADMIN.equals(userRole) && !loginUser.getId().equals(userId),ErrorCode.NO_AUTH);
        //todo 4. 移出所有加入博客的关联信息


        //5. 删除队伍
        boolean b = this.removeById(id);
//        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR,"删除博客失败");
        return b;
    }

    @Override
    public boolean updateBlog(BlogUpdateRequest blogUpdateRequest, User loginUser) {
        //怎么更新博客呢？
        ThrowUtils.throwIf(blogUpdateRequest == null,ErrorCode.PARAMS_ERROR);
        Long blogId = blogUpdateRequest.getId();
        ThrowUtils.throwIf(blogId == null || blogId <= 0,ErrorCode.PARAMS_ERROR,"博客不存在");
        Integer userRole = loginUser.getRole();
        Blog oldBlog = this.getById(blogId);
        Long userId = oldBlog.getUserId();
        //只有博客的作者可以修改
        ThrowUtils.throwIf(!loginUser.getId().equals(userId),ErrorCode.NO_AUTH);
        //修改博客
        String name = blogUpdateRequest.getTitle();
        if (name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客标题过长");
        }
        //(2)图片最多为9张，多张以"/"隔开
        String images = blogUpdateRequest.getImages();
        String[] imagess = images.split("/");
        if(imagess.length > 9){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"图片数量过多");
        }
        // (3) 0 <= 博客内容 <= 512
        String content = blogUpdateRequest.getContent();
        if (content.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客内容过长");
        }
        if(content.length() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"博客内容不能为空");
        }
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogUpdateRequest,blog);
        boolean b = this.updateById(blog);
        return b;
    }
}




