package com.example.CampusPartnerBackend.modal.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类（脱敏）
 */
@Data
public class UserVO implements Serializable {
    
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String user_account;

    /**
     * 用户头像
     */
    private String Avatar_url;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 状态 0 - 正常
     */
    private Integer Status;

    /**
     * 创建时间
     */
    private Date Create_time;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer Role;

    /**
     * 星球编号
     */
    private String planetCode;

    private static final long serialVersionUID = 1L;
}