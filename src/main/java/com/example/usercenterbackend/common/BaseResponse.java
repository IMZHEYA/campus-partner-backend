package com.example.usercenterbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基本返回值类型
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 7651460044723894533L;

    private int code;

    private T data;

    private String message;

    private final String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this(code,data,"", "");
    }

    public BaseResponse(int code,T data,String message){
        this(code,data,message,"");
    }
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(), errorCode.getDescription());

    }
    public BaseResponse(ErrorCode errorCode,String message,String description){
        this(errorCode.getCode(),null,message, description);
    }

    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(),null, errorCode.getMessage(), description);
    }
}
