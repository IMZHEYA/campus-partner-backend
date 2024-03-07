package com.example.usercenterbackend.modal.domain.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -7622407616477127669L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
