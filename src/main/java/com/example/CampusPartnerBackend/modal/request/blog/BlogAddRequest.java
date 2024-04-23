package com.example.CampusPartnerBackend.modal.request.blog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class BlogAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 图片，最多9张，多张以","隔开
     */
    private String images;

    /**
     * 文章
     */
    private String content;

}