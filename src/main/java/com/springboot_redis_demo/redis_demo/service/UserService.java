package com.springboot_redis_demo.redis_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.springboot_redis_demo.redis_demo.entity.User;
import com.springboot_redis_demo.redis_demo.mapper.UserMapper;

import com.springboot_redis_demo.redis_demo.request.EmployeeRequest;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Elvis
 * @Description:
 * @Date: 2019/12/29 15:28
 */
@Service
public class UserService {

    @Autowired
    private Environment env;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public User getUserInfoV2(Integer userId) throws Exception{

        final String key = String.format(env.getProperty("redis.user.info.key"),userId);
        User user;
        if (stringRedisTemplate.hasKey(key)) {
            //key存在于缓存
            String value = stringRedisTemplate.opsForValue().get(key);
            user = objectMapper.readValue(value, User.class);

        } else {
            //key不存在于缓存->查数据库并存入缓存
             user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user));
            }
        }
        return user;
    }

    public User getUserInfoV3(Integer userId) throws Exception{

        final String key = String.format(env.getProperty("redis.user.info.key"),userId);
        User user;
        if (stringRedisTemplate.hasKey(key)) {
            //key存在于缓存
            String value = stringRedisTemplate.opsForValue().get(key);
            user = objectMapper.readValue(value, User.class);

        } else {
            //key不存在于缓存->查数据库并存入缓存
            user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user), env.getProperty("redis.user.info.timeout", Long.class), TimeUnit.MINUTES);
            }
        }
        return user;
    }

    public User getUserInfoV4(Integer userId) throws Exception{

        final String key = String.format(env.getProperty("redis.user.info.key"),userId);
        User user = null;
        if (stringRedisTemplate.hasKey(key)) {
            //key存在于缓存
            String value = stringRedisTemplate.opsForValue().get(key);
            if(!Strings.isNullOrEmpty(value)){
                user = objectMapper.readValue(value, User.class);
            }


        } else {
            //key不存在于缓存->查数据库并存入缓存
            user = userMapper.selectByPrimaryKey(userId);
            long expire = RandomUtils.nextLong(10, 30);
            if (user != null) {
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user), expire, TimeUnit.SECONDS);

            }else{
                stringRedisTemplate.opsForValue().set(key,"",expire, TimeUnit.SECONDS);
            }
            log.info("过期的随机时间:{}", expire);
        }
        return user;
    }

    public void updateCache(Integer userId) {
        try {
            User user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                final String key = String.format(env.getProperty("redis.user.info.key"),userId);

                stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(user), RandomUtils.nextLong(10, 30), TimeUnit.MINUTES);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 用户详情-hash散列存储
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV5(Integer userId) throws Exception {
        final String key = env.getProperty("redis.user.info.hash.key");
        HashOperations<String, String, User> hashOperations = redisTemplate.opsForHash();
        User user;
        if (hashOperations.hasKey(key,String.valueOf(userId))) {
/*            Map<String, User> userMap = new HashMap<>();
            userMap.put("1", userMapper.selectByPrimaryKey(1));*/
            user = hashOperations.get(key,String.valueOf(userId));
        } else {
            user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                hashOperations.putIfAbsent(key,String.valueOf(userId),user);
            } else {
                hashOperations.putIfAbsent(key,String.valueOf(userId),user);
            }
        }
        return user;
    }

    /**
     * 避免缓存穿透
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV6(Integer userId) throws Exception {
        final String key = env.getProperty("redis.user.info.hash.key");
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        User user = null;
        if (hashOperations.hasKey(key,String.valueOf(userId))) {
/*            Map<String, User> userMap = new HashMap<>();
            userMap.put("1", userMapper.selectByPrimaryKey(1));*/
            String value = hashOperations.get(key,String.valueOf(userId));
            if(!Strings.isNullOrEmpty(value)){
                user = objectMapper.readValue(value, User.class);
            }
        } else {
            user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                hashOperations.putIfAbsent(key,String.valueOf(userId),objectMapper.writeValueAsString(user));
            } else {
                hashOperations.putIfAbsent(key,String.valueOf(userId),"");
            }
        }
        return user;
    }

}
