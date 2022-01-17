package com.itlhh.exception;

/**
 * @Author lhh
 * @Date 2022/1/17 17:23
 */

/**
 * 自定义业务异常类
 */
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }

}
