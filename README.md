# 克迪网上商城

一个基于 Vue 3 + Spring Boot 的前后端分离日用品网上商城系统。

## 项目结构

```
by/
├── backend/          # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/kedi/shop/
│   │       │   ├── controller/    # 控制器层
│   │       │   ├── service/      # 服务层
│   │       │   ├── mapper/       # 数据访问层
│   │       │   ├── entity/       # 实体类
│   │       │   ├── config/       # 配置类
│   │       │   └── util/         # 工具类
│   │       └── resources/
│   │           ├── application.yml
│   │           └── sql/init.sql   # 数据库初始化脚本
│   └── pom.xml
│
└── frontend/         # Vue 3 前端
    ├── src/
    │   ├── api/        # API 接口
    │   ├── assets/     # 静态资源
    │   ├── router/     # 路由配置
    │   ├── stores/     # Pinia 状态管理
    │   └── views/       # 页面组件
    ├── package.json
    └── vite.config.js
```

## 技术栈

### 后端
- JDK 21
- Spring Boot 3.2.4
- MyBatis-Plus 3.5.6
- MySQL 8.0
- JWT (jjwt 0.12.5)
- Druid 连接池

### 前端
- Vue 3
- Vite 5
- Vue Router 4
- Pinia 2
- Element Plus 2.7
- Axios

## 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+

## 快速开始

### 1. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source c:/Users/21193/Desktop/by/backend/src/main/resources/sql/init.sql
```

或者直接在 MySQL Workbench 或 Navicat 中执行 `sql/init.sql` 文件。

### 2. 启动后端

```bash
# 进入后端目录
cd c:/Users/21193/Desktop/by/backend

# 使用 Maven 构建并运行
mvn spring-boot:run
```

后端启动后运行在 http://localhost:8080

### 3. 启动前端

```bash
# 新开一个终端，进入前端目录
cd c:/Users/21193/Desktop/by/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问 http://localhost:3000

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

## 功能列表

### 用户端
- [x] 用户注册
- [x] 用户登录
- [x] 商品浏览
- [x] 商品搜索
- [x] 分类筛选
- [x] 商品详情
- [x] 购物车管理
- [x] 订单提交
- [x] 订单查询
- [x] 个人信息管理
- [x] 修改密码

### 功能演示
1. 打开前端页面，无需登录可浏览商品
2. 点击任意商品可查看详情
3. 登录后可将商品加入购物车
4. 在购物车中可修改数量、删除商品
5. 确认订单并填写收货信息后提交
6. 在个人中心可查看订单、管理个人信息

## 数据库配置

配置文件位置: `backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kedi_shop?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 123456
```

如果你的 MySQL 配置不同，请修改上述配置。

## API 接口

后端 API 基础路径: `/api`

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 用户 | /user/login | POST | 用户登录 |
| 用户 | /user/register | POST | 用户注册 |
| 用户 | /user/info | GET | 获取用户信息 |
| 分类 | /category/list | GET | 获取分类列表 |
| 商品 | /product/list | GET | 获取商品列表 |
| 商品 | /product/detail/{id} | GET | 获取商品详情 |
| 购物车 | /cart/list | GET | 获取购物车列表 |
| 购物车 | /cart/add | POST | 加入购物车 |
| 订单 | /order/create | POST | 创建订单 |
| 订单 | /order/list | GET | 获取订单列表 |

## 项目截图

系统包含以下页面:
- 首页 (商品展示、分类导航、轮播图)
- 商品列表页 (分类筛选、搜索、排序、分页)
- 商品详情页 (商品信息、加入购物车)
- 购物车页面 (数量修改、删除、清空、结算)
- 订单确认页 (收货信息填写)
- 订单列表页 (订单状态展示)
- 个人中心 (信息管理、密码修改)
- 登录/注册页面
