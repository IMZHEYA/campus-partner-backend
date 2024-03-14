package com.example.CampusPartnerBackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    @Resource
    private RedissonClient redissonClient;

    // 秒-分-时-日-月-年
    @Scheduled(cron = "0 13 18 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("cp:job:PreCacheJob:doCache:job");
        try {
            //能抢到锁，才执行方法
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                for(Long userId : mainUserList){
                    String redisKey = String.format("Campus:partner:%s",userId);
                    // 从数据库中查
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
        } catch (Exception e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }
}
