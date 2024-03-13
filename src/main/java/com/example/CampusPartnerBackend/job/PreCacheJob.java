package com.example.CampusPartnerBackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Component //记得要加载成bean
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;
    //重点用户的id
    List<Long> mainUserList = Arrays.asList(1L);
    // 秒-分-时-日-月-年
    @Scheduled(cron = "0 13 18 * * *")
    public void doCacheRecommendUser(){
        for(Long userId : mainUserList){
            String redisKey = String.format("Campus:partner:%s",userId);
//        从数据库中查
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            //pageNum当前请求页码   pageSize每页数据条数
            Page<User> page = userService.page(new Page<>(1, 20), queryWrapper);
            //写入缓存
            try {
                redisTemplate.opsForValue().set(redisKey,page,20, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("set redisKey error",e);
            }
        }
    }
}
