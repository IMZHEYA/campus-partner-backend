package com.example.CampusPartnerBackend.modal.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 4098140048115029595L;
    private String userAccount;
    private String userPassword;
}
