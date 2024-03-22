## 项目介绍

校园伙伴匹配，一个前后端分离的用户匹配系统，前端使用 **Vite + Vue3 + Vant4** ，后端使用 **SpringBoot + MyBatis-Plus + MySQL + Redis** 等技术。

#### 前端地址:



#### 后端地址:



## 在线体验





## 项目背景



许多热爱学习竞赛的校园同学在参加比赛时常常面临没有队友的问题，这使得他们很难发挥自己的实力，也很难取得好成绩。同时，许多用户在寻找队友时也面临一些困难，比如无法找到合适的人选，或者无法与潜在的队友进行充分的交流和沟通。因此，我们希望通过该网站，为这些用户提供一个交流平台，帮助他们找到理想的队友，组建强大的竞赛团队，参加各种校园比赛，并最终实现自己的梦想。

## 核心功能



1. 用户注册和登录：用户可以通过注册账号并登录使用该网站。
2. 标签匹配：用户可以选择自己的技能和需求标签，系统会根据标签匹配合适的队友。
3. 组队功能：用户可以与其他用户组建队伍，一起参加校园竞赛。

## 项目亮点

1. 用户登录：使用 Redis 实现分布式 Session，解决集群间登录态同步问题；
2. 对于项目中复杂的集合处理（比如为队伍列表关联已加入队伍的用户），使用 Java 8 Stream API 和 Lambda 表达式来简化编码。
3. 使用 Redis 缓存首页高频访问的用户信息列表，将接口响应时长从 12520ms缩短至400ms。且通过自定义 Redis 序列化器来解决数据乱码、空间浪费的问题。
4. 为解决首次访问系统的用户主页加载过慢的问题，使用定时任务来实现缓存预热，并通过分布式锁保证多机部署时定时任务不会重复执行。
5. 为解决同一用户重复加入队伍、入队人数超限的问题，使用 Redisson 分布式锁来实现操作互斥，保证了接口幂等性。
6. 使用编辑距离算法实现了根据标签匹配最相似用户的功能，并通过优先队列来减少 TOP N 运算过程中的内存占用。
7. 使用 Knife4j + Swagger 自动生成后端接口文档

## 技术选型



**前端**

- Vue 3
- Vite 脚手架
- Vant4 UI 移动端组件库
- Axios 请求库

**后端**

- JAVA SpringBoot 框架
- MySQL 数据库
- Mybatis-Plus
- Mybatis X 根据数据库表生成对应的mapper、service、model代码
- Redis 缓存
- Redisson 分布式锁
- 定时任务
- Swagger + Knife4j 接口文档
- Gson JSON序列化库
- 最短编辑距离算法
 ## 功能介绍



### 模拟数据

首次启动可执行测试类中的方法生成一定数量的随机用户模拟数据。

### 主页

1）匹配用户：用户未登录时为随机查询，用户登录后若用户填写了标签，则将根据标签的相似度进行匹配。

### 用户注册



1）用户未登录时点击个人页将跳转至登录页。



### 个人主页

1）注册成功后或登陆后可以进入个人页，用户可点击修改信息修改除登录账号外的信息。

2）创建的队伍：用户创建的队伍信息。

3）加入的队伍：用户加入的队伍信息。

### 修改信息

1）昵称修改：用户可修改展示的昵称。

2）标签修改：用户可以更新自己的标签。

3）性别修改：用户可修改性别。

4）电话修改：用户修改为新手机号。

### 搜索用户

1）标签搜索：用户可以点击右上角的搜索按钮，通过标签搜索用户。

### 创建队伍

1）点击队伍页右下角的加号可以跳转至创建队伍页面。

2）若不填写过期时间则队伍永久有效。

3）公开状态所有人可加入，私有状态仅自己可见，加密状态其他用户输入正确的密码则可以加入。

### 队伍详情

1）在队伍页可以看到非私有队伍，队长和管理员可以更新队伍和解散队伍。加入加密队伍需要输入密码。

2）搜索队伍：输入队伍名可搜索队伍。



## 部分界面展示

### 登录界面
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/d5cfe013-8782-4524-8f50-f8111ad4a334)
### 主页推荐
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/98fa330c-2656-403e-a40e-aa79789e393c)

### 心动模式
（推荐标签相似用户）

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/c5349b52-f13d-4856-b907-e2aa87d75675)

### 搜索用户
指定标签

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/55465149-b95b-40f5-a395-f5efd67d152a)

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/787d6ea0-e6fd-42e8-9466-cdb4b2345b52)
搜索结果

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/8785c23e-54f8-40b7-b39b-c43a3b54a270)
### 队伍页面

可以看到公开和加密的队伍，私有队伍目前不可见

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/4d74cc57-dbe6-4f59-800c-a44f3102e201)

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/d74bff10-d504-4f8e-83fd-369d30483f1f)

创建人可见更新队伍，解散队伍，非创建人只能看见加入队伍或者退出队伍

### 更新队伍
更新成加密队伍，要设置密码

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/2fe4de58-1608-4e3a-bb83-781937dfc60f)
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/bf9493eb-b411-4db9-b06e-9390db6ef73b)
### 个人信息
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/997c583a-7747-43c7-af7e-3a1b9165b1d3)
### 修改信息
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/0162b920-c46a-4f76-bada-fe84d0248dfc)

除账号，编号，注册时间和头像暂不可修改，其他皆可修改
（比如修改昵称）

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/4869ea6d-2249-4e7a-80a8-ce6df63d4bd6)
### 查看创建的队伍
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/a0fb3957-8cdc-48d0-a197-5cb72f132975)

同时可在此创建队伍（可以设置加入的最大人数）

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/4f677c7a-07fa-40aa-8a85-56c9ca62fda5)
### 查看加入的队伍
![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/62697419-db80-4ee6-833d-48f074787de9)
（换个号）

![image](https://github.com/Serendipityzhezi/campus-partner-backend/assets/122675076/e322b790-7322-4f45-a48c-a7aac5c7b2f8)

（貌似还有一丢丢小bug 在查看已加入队伍页面，队伍人数为null，还有不显示已加入的队伍退出队伍）
