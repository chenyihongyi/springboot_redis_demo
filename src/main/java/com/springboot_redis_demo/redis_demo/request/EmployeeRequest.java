package com.springboot_redis_demo.redis_demo.request;

import lombok.Data;
import lombok.ToString;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2019/12/29 19:54
 */
@Data
@ToString
public class EmployeeRequest {

    private Integer id;

    @NotBlank
    private String userName;

    private String password;

    @NotBlank
    private String posName;

    private Integer age;

    private String mobile;

    private String profile;

}
