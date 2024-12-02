package com.xuecheng.base.exeception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 全局异常处理器
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@ControllerAdvice  //控制器增强注解
//@RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    @ResponseBody //将信息返回为json格式
    @ExceptionHandler(XueChengPlusException.class)   //此方法捕获 XueChengPlusException 异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //状态码返回500
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {

        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    //MethodArgumentNotValidException
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        List<String> errorlist = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item ->{
            errorlist.add(item.getDefaultMessage());
        });

        String errorMessages = StringUtils.join(errorlist, ",");

        log.error("【系统异常】{}",e.getMessage(),errorMessages);
        return new RestErrorResponse(errorMessages);
    }
}

