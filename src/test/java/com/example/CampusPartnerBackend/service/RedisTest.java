package com.example.CampusPartnerBackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;
    @Test
    void test(){
//        stringRedisTemplate.opsForValue().set("7979","牛逼");
        redisTemplate.opsForValue().set("99999","帅啊");
    }
}
