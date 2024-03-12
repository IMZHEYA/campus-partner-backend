package com.example.CampusPartnerBackend.service;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.CampusPartnerBackend.Mapper.UserMapper;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));


    @Test
    void test(){
        User user = new User();
        user.setUsername("yupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://profile-avatar.csdnimg.cn/9751285f58b94575a8a50bad240cc012_m0_74870396.jpg!1");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("321");
        user.setUserCode("1");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);

    }


    @Test
    public void searchUsersBytags() {
        List<String> tags = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersBytags(tags);
        Assertions.assertNotNull(userList);
    }


    @Test
    void test1(){
        //期望返回1但实际返回了多条
//        User user = userMapper.selectOne(null);
    }

    @Test    void insertUsers1(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int num = 10000;
        List<User> userList = new ArrayList<>();
        for(int i = 0; i <= num; i ++){
            User user = new User();
            user.setUsername("");
            user.setUserAccount("1234");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("321");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setUserCode("");
            user.setTags("[]");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void insertUsers2(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int num = 10000;
        List<User> userList = new ArrayList<>();
        for(int i = 0; i <= num; i ++){
            User user = new User();
            user.setUsername("");
            user.setUserAccount("1234");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("321");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setUserCode("");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    @Test
    void insertUsers3(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int num = 100000;
        int j = 0;
        //  // 创建一个用于保存异步任务的 CompletableFuture 列表
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int i = 0; i < 10; i ++){
            // 创建一个线程安全的用户列表
            List<User> userList = Collections.synchronizedList(new ArrayList<>());
                while (true) {
                    j++;
                    User user = new User();
                    user.setUsername("");
                    user.setUserAccount("1234");
                    user.setAvatarUrl("https://profile-avatar.csdnimg.cn/9751285f58b94575a8a50bad240cc012_m0_74870396.jpg!1");
                    user.setGender(0);
                    user.setUserPassword("12345678");
                    user.setPhone("123");
                    user.setEmail("321");
                    user.setCreateTime(new Date());
                    user.setUpdateTime(new Date());
                    user.setUserCode("");
                    user.setTags("[]");
                    userList.add(user);
                    if (j % 10000 == 0) {
                        break;
                    }
                }
                //  //异步执行 使用CompletableFuture开启异步任务
                CompletableFuture future = CompletableFuture.runAsync(()->{
                    System.out.println(Thread.currentThread().getName());
                    userService.saveBatch(userList, 100);
                },executorService);

            futureList.add(future);
        }
        // 等待所有异步任务执行完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}