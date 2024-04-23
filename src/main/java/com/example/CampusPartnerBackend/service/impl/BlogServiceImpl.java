package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.Blog;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.service.BlogService;
import com.example.CampusPartnerBackend.mapper.BlogMapper;
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
        // (3) 博客内容 <= 512
        String content = blog.getContent();
        if (content.length() > 0 && content.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客内容过长");
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
}




