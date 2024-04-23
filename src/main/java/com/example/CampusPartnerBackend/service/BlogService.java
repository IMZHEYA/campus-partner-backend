package com.example.CampusPartnerBackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.CampusPartnerBackend.modal.domain.Blog;
import com.example.CampusPartnerBackend.modal.domain.User;

/**
* @author 13425
* @description 针对表【blog】的数据库操作Service
* @createDate 2024-04-22 19:20:50
*/
public interface BlogService extends IService<Blog> {

    Long addBlog(Blog blog, User loginUser);
}
