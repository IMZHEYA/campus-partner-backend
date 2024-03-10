package com.example.CampusPartnerBackend.exception;

import com.example.CampusPartnerBackend.common.BaseResponse;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHanler(BusinessException e){
        log.info("BusinessException： " + e.getMessage() ,e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHanler(RuntimeException e){
        log.info("RuntimeException： " + e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),e.getMessage());
    }
}
