package com.example.CampusPartnerBackend.modal.dto;

import com.example.CampusPartnerBackend.common.pageRequest;
import lombok.Data;

/**
 * 队伍查询封装类
 */
@Data
public class TeamQuery extends pageRequest{
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
    /**
     * 搜索关键词
     */
    private String searchText;
}