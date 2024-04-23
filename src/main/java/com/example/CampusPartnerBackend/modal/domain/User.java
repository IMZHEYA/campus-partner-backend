package com.example.CampusPartnerBackend.modal.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户密码
     */
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 用户头像
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 性别 0-女 1-男 2-保密
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 用户状态，0为正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 用户角色 0-普通用户,1-管理员
     */
    @TableField(value = "role")
    private Integer role;

    /**
     * 
     */
    @TableField(value = "friend_ids")
    private String friendIds;

    /**
     * 标签列表
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 确认密码
     */
    @TableField(value = "check_password")
    private String checkPassword;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}