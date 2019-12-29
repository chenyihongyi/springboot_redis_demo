package com.springboot_redis_demo.redis_demo.mapper;

import com.springboot_redis_demo.redis_demo.entity.User;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2019/12/29 1:54
 */
public interface UserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
