package com.springboot_redis_demo.redis_demo.response;

import com.springboot_redis_demo.redis_demo.enums.StatusCode;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2019/12/29 15:09
 */
public class BaseResponse<T> {

    private Integer code;
    private String msg;
    private T data;

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
