# 🍊 果漾鲜生 FruitFresh — 水果销售平台（Spring Boot 3 + Vue 3 全栈项目）

一个面向求职的作品级全栈项目：**用户在线买水果 + 管理员后台管理**，功能闭环、代码规范、技术栈贴合企业级开发，同时结构清晰不过度复杂。

---

## ✨ 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Spring Boot | 3.2.5 |
| 后端 | Java | 17 |
| 持久层 | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis | 5+ |
| 消息队列 | RabbitMQ | 3.x |
| 安全 | Spring Security + JWT (jjwt) | 6.x / 0.11.5 |
| 接口文档 | Knife4j (OpenAPI3) | 4.4.0 |
| 前端 | Vue 3 + Vite 5 + Pinia + Vue Router | — |
| UI 组件 | Element Plus + ECharts | — |

## 🚀 核心功能

**用户端（C 端商城）**
- 注册 / 登录（JWT 认证，BCrypt 加密）
- 首页轮播图、分类导航、热销推荐
- 商品列表（分类筛选 + 关键词搜索 + 分页）
- 商品详情、加入购物车、立即购买
- 购物车（数量修改、勾选、删除、失效商品清理）
- 下单结算（收货地址选择、库存校验、金额计算）
- 订单中心（待付款 / 待发货 / 待收货 / 已完成 / 已取消 + 模拟支付 + 确认收货 + 取消）
- 收货地址管理（增删改、设默认）
- 个人中心（资料修改、改密码）

**管理端（Admin 后台）**
- 数据看板（用户 / 商品 / 订单 / 销售额统计 + 近7天销售趋势 + 分类销售占比图表）
- 商品管理（增删改查、上架/下架、热销开关）
- 分类管理、轮播图管理
- 订单管理（列表筛选、详情、发货）
- 用户管理（搜索、启用/禁用）

**企业级亮点**
- ✅ 统一响应体 `Result` + 全局异常处理
- ✅ Spring Security 无状态 JWT 认证，`/admin/**` 角色权限隔离
- ✅ Redis 缓存分类、热销、商品详情（失效策略：写操作即删缓存）
- ✅ RabbitMQ **延迟队列**：订单 30 分钟未支付自动取消，库存自动回滚
- ✅ MyBatis-Plus 逻辑删除 + 自动填充（create_time / update_time）
- ✅ 参数校验（@Valid）、AOP 接口日志、接口文档（Knife4j）
- ✅ 事务管理：下单过程全部在一个事务内（明细 + 扣库存 + 清购物车）

---

## 📁 目录结构

```
fruit-fresh/
├── fruit-server/                  # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/fruitfresh/
│       │   ├── common/            # 统一响应、状态码、业务异常、全局异常
│       │   ├── config/            # Security / Redis / RabbitMQ / MyBatis-Plus / 跨域 / Knife4j / 数据初始化 / AOP
│       │   ├── controller/        # 用户端接口
│       │   ├── controller/admin/  # 管理端接口
│       │   ├── dto/               # 请求对象
│       │   ├── entity/            # 实体（t_user / t_product / ...）
│       │   ├── mapper/            # MyBatis-Plus Mapper（含统计SQL）
│       │   ├── mq/                # RabbitMQ 超时关单消费者
│       │   ├── security/          # JWT 工具、认证过滤器、登录用户
│       │   ├── service/           # 业务层
│       │   └── vo/                # 视图对象
│       └── resources/
│           ├── application.yml
│           └── sql/fruit_fresh.sql   # 建库脚本 + 初始化数据
└── fruit-web/                     # Vue 3 前端（用户端 + 管理端）
    └── src/
        ├── api/                   # axios 封装 + 接口模块
        ├── components/            # 导航栏、商品卡片
        ├── router/                # 路由 + 登录/管理员守卫
        ├── stores/                # Pinia（用户状态）
        ├── utils/                 # 格式化工具
        └── views/                 # 用户端页面 + admin/ 管理端页面
```

---

## 🔧 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | 必须 |
| Maven | 3.6+ | 已配即可 |
| MySQL | 8.x | 本地或 Docker |
| Redis | 5+ | 本地或 Docker |
| RabbitMQ | 3.x | 本地或 Docker（management 插件） |
| Node.js | 18+ | 推荐 20/22 |

> 没有 Docker 也可以直接用压缩包安装 MySQL / Redis / RabbitMQ（Windows 教程很多）。

---

## 🚀 启动步骤

### 1. 初始化数据库

用 Navicat / DataGrip / 命令行执行：

```
fruit-server/src/main/resources/sql/fruit_fresh.sql
```

会创建 `fruit_fresh` 库、8 张表并写入分类 / 商品 / 轮播图演示数据。

### 2. 修改后端配置

打开 `fruit-server/src/main/resources/application.yml`，按本机环境修改：

```yaml
spring:
  datasource:
    username: root        # 你的 MySQL 用户名
    password: root        # 你的 MySQL 密码
  data:
    redis:
      host: localhost     # Redis 地址（无密码则 password 留空）
  rabbitmq:
    username: guest       # RabbitMQ 默认账号
    password: guest
```

### 3. 启动后端

用 IDEA 打开 `fruit-server` 文件夹，等待 Maven 导入依赖后运行：

```
com.fruitfresh.FruitFreshApplication
```

启动成功会自动创建管理员账号 `admin / admin123` 和演示账号 `demo / 123456`。

接口文档：http://localhost:8080/api/doc.html

### 4. 启动前端

```bash
cd fruit-web
npm install
npm run dev
```

浏览器自动打开 http://localhost:5173

---

## 🧪 演示账号

| 角色 | 账号 | 密码 | 入口 |
|------|------|------|------|
| 普通用户 | demo | 123456 | 商城直接登录 |
| 管理员 | admin | admin123 | 登录后自动进入 / 右上角「管理后台」 |

---

## 🧠 核心设计说明（面试可讲）

### 1. JWT 认证流程
登录成功签发 JWT（含 userId / userType）→ 前端每次请求带 `Authorization: Bearer xxx` → `JwtAuthenticationFilter` 解析校验并写入 SecurityContext → `/admin/**` 由 SecurityConfig 强制 `hasRole("ADMIN")`。

### 2. 订单超时自动取消（RabbitMQ 延迟队列）
下单后发送消息到 `fruit.order.delay.exchange` → 进入带 **30 分钟 TTL** 的延迟队列 → 消息过期进入死信交换机 → 路由到超时队列 → 消费者查询订单，若仍为"待付款"则取消并**回滚库存**。MQ 故障时下单主流程不受影响（catch 记录日志）。

### 3. Redis 缓存策略
- `fruit:category:list`（30分钟）、`fruit:hot:products`（10分钟）、`fruit:product:{id}`（10分钟）
- 管理端写操作（增删改、上下架、热销）后主动删除缓存，保证一致性

### 4. 数据库设计要点
- 订单明细对商品做**快照**（名称/图片/单价），商品改价不影响历史订单
- 逻辑删除字段 `deleted` 全局生效，管理端删除不物理删除
- 订单状态机：`0待付款 → 1待发货 → 2待收货 → 3已完成`，`0 → 4已取消`（手动或超时）

---

## 📚 常见问题

**Q：启动后端报数据库连接失败？**
检查 MySQL 是否启动、`application.yml` 的账号密码是否正确、是否已执行 SQL 脚本。

**Q：RabbitMQ 没装，下单会失败吗？**
不会。下单主流程正常，只是"超时自动取消"功能失效，控制台会打印警告日志。

**Q：商品图片不显示？**
SQL 中默认使用 unsplash 在线图片，若网络不可用，前端会自动显示水果色块兜底图。可在管理后台把图片地址换成自己的图床。

**Q：前端端口冲突？**
修改 `fruit-web/vite.config.js` 中的 `server.port`。
