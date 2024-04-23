package com.example.CampusPartnerBackend.service;
import java.util.*;

import com.example.CampusPartnerBackend.mapper.UserMapper;
import com.example.CampusPartnerBackend.modal.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.concurrent.*;

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
            user.setUsername("杏儿");
            user.setUserAccount("1234");
            user.setAvatarUrl("https://img2.baidu.com/it/u=4107924847,69017323&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1711126800&t=74d96cfa7d3219cc50b4d1e0e071d551");
            user.setGender(0);
            user.setUserPassword("12345678" + i);
            user.setPhone("1232558165@qq.com");
            user.setEmail("321");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
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