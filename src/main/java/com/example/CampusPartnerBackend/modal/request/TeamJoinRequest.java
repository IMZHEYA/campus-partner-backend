package com.example.CampusPartnerBackend.modal.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 *
 * @author hunhaowudiya
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = -24663018187059425L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}