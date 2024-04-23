package com.example.CampusPartnerBackend.modal.request.user;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -7622407616477127669L;
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 确认用户密码
     */
    private String checkPassword;
}
