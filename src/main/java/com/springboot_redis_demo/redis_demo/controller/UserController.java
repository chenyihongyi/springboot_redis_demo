package com.springboot_redis_demo.redis_demo.controller;

import com.springboot_redis_demo.redis_demo.entity.User;
import com.springboot_redis_demo.redis_demo.enums.StatusCode;
import com.springboot_redis_demo.redis_demo.mapper.UserMapper;
import com.springboot_redis_demo.redis_demo.request.EmployeeRequest;
import com.springboot_redis_demo.redis_demo.response.BaseResponse;
import com.springboot_redis_demo.redis_demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2019/12/29 2:02
 */
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String prefix = "user";

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取详情- 解决缓存穿透
     * @return
     */
    @RequestMapping(value = prefix + "/detail/{userId}", method = RequestMethod.GET)
    public BaseResponse detail(@PathVariable Integer userId) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        if (userId < 0) {
            return new BaseResponse(StatusCode.Invalid_Params);
        }
        try {
           // response.setData(userService.getUserInfoV2(userId));
           // response.setData(userService.getUserInfoV3(userId));
           // response.setData(userService.getUserInfoV4(userId));
            //response.setData(userService.getUserInfoV5(userId));
            response.setData(userService.getUserInfoV6(userId));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail);
            log.error(e.getMessage());
        }
        return response;
    }

    /**
     * 新增-更新用户信息
     * @param employeeRequest
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix + "/insert/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //@Transactional(rollbackFor = Exception.class)
    public BaseResponse insertUpdate(@RequestBody @Validated EmployeeRequest employeeRequest,BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.Invalid_Params);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            if (employeeRequest.getId() != null && employeeRequest.getId() > 0) {
                if (employeeRequest.getId() != null && employeeRequest.getId() > 0) {
                    User entity = userMapper.selectByPrimaryKey(employeeRequest.getId());
                        BeanUtils.copyProperties(employeeRequest,entity);
                        userMapper.updateByPrimaryKeySelective(entity);

                        userService.updateCache(entity.getId());

                }
            } else {
                User user = new User();
                BeanUtils.copyProperties(employeeRequest,user);
                userMapper.insertSelective(user);

                userService.updateCache(user.getId());
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail);
            log.error(e.getMessage());
        } return response;
    }

}
