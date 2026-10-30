-- =============================================================
-- 果漾鲜生 FruitFresh 水果销售网站 数据库初始化脚本
-- 适用：MySQL 8.x   字符集：utf8mb4
-- 说明：管理员账号(admin/admin123)与演示账号(demo/123456)
--       由后端 DataInitializer 启动时自动创建（密码 BCrypt 加密）
-- =============================================================

CREATE DATABASE IF NOT EXISTS fruit_fresh DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE fruit_fresh;

-- -------------------------------------------------------------
-- 1. 用户表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    user_type   TINYINT      NOT NULL DEFAULT 1 COMMENT '用户类型 1普通用户 2管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '用户表';

-- -------------------------------------------------------------
-- 2. 商品分类表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon        VARCHAR(255) DEFAULT NULL COMMENT '分类图标(emoji或图片)',
    sort        INT         NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    status      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
    deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '商品分类表';

-- -------------------------------------------------------------
-- 3. 商品表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_product;
CREATE TABLE t_product (
    id             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    name           VARCHAR(100)   NOT NULL COMMENT '商品名称',
    category_id    BIGINT         NOT NULL COMMENT '分类ID',
    image          VARCHAR(255)   DEFAULT NULL COMMENT '商品主图',
    description    VARCHAR(1000)  DEFAULT NULL COMMENT '商品描述',
    price          DECIMAL(10, 2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
    stock          INT            NOT NULL DEFAULT 0 COMMENT '库存',
    sales          INT            NOT NULL DEFAULT 0 COMMENT '销量',
    hot            TINYINT        NOT NULL DEFAULT 0 COMMENT '是否热销 1是 0否',
    status         TINYINT        NOT NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
    deleted        TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id)
) ENGINE = InnoDB COMMENT = '商品表';

-- -------------------------------------------------------------
-- 4. 轮播图表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_banner;
CREATE TABLE t_banner (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    image       VARCHAR(255) NOT NULL COMMENT '轮播图地址',
    title       VARCHAR(100) DEFAULT NULL COMMENT '标题',
    link_url    VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '轮播图表';

-- -------------------------------------------------------------
-- 5. 购物车表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_cart;
CREATE TABLE t_cart (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    product_id  BIGINT   NOT NULL COMMENT '商品ID',
    quantity    INT      NOT NULL DEFAULT 1 COMMENT '数量',
    checked     TINYINT  NOT NULL DEFAULT 1 COMMENT '是否勾选 1是 0否',
    deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id)
) ENGINE = InnoDB COMMENT = '购物车表';

-- -------------------------------------------------------------
-- 6. 收货地址表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_address;
CREATE TABLE t_address (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id         BIGINT       NOT NULL COMMENT '用户ID',
    receiver_name   VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    receiver_phone  VARCHAR(20)  NOT NULL COMMENT '收货人电话',
    province        VARCHAR(50)  NOT NULL COMMENT '省',
    city            VARCHAR(50)  NOT NULL COMMENT '市',
    district        VARCHAR(50)  NOT NULL COMMENT '区/县',
    detail          VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认 1是 0否',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id)
) ENGINE = InnoDB COMMENT = '收货地址表';

-- -------------------------------------------------------------
-- 7. 订单表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_orders;
CREATE TABLE t_orders (
    id                BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no          VARCHAR(32)    NOT NULL COMMENT '订单号',
    user_id           BIGINT         NOT NULL COMMENT '用户ID',
    total_amount      DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    status            TINYINT        NOT NULL DEFAULT 0 COMMENT '订单状态 0待付款 1待发货 2待收货 3已完成 4已取消',
    receiver_name     VARCHAR(50)    NOT NULL COMMENT '收货人姓名',
    receiver_phone    VARCHAR(20)    NOT NULL COMMENT '收货人电话',
    receiver_address  VARCHAR(300)   NOT NULL COMMENT '收货地址(省市区+详细)',
    remark            VARCHAR(200)   DEFAULT NULL COMMENT '订单备注',
    pay_time          DATETIME       DEFAULT NULL COMMENT '支付时间',
    delivery_time     DATETIME       DEFAULT NULL COMMENT '发货时间',
    finish_time       DATETIME       DEFAULT NULL COMMENT '完成时间',
    deleted           TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time       DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user (user_id),
    KEY idx_status (status)
) ENGINE = InnoDB COMMENT = '订单表';

-- -------------------------------------------------------------
-- 8. 订单明细表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS t_order_item;
CREATE TABLE t_order_item (
    id            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id      BIGINT         NOT NULL COMMENT '订单ID',
    product_id    BIGINT         NOT NULL COMMENT '商品ID',
    product_name  VARCHAR(100)   NOT NULL COMMENT '商品名称(下单时快照)',
    product_image VARCHAR(255)   DEFAULT NULL COMMENT '商品图片(下单时快照)',
    price         DECIMAL(10, 2) NOT NULL COMMENT '成交单价',
    quantity      INT            NOT NULL COMMENT '购买数量',
    total_price   DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    deleted       TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
    create_time   DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order (order_id)
) ENGINE = InnoDB COMMENT = '订单明细表';

-- =============================================================
-- 初始化数据
-- =============================================================

-- 分类
INSERT INTO t_category (name, icon, sort, status) VALUES
('时令鲜果', '🍉', 1, 1),
('进口水果', '🥝', 2, 1),
('浆果莓类', '🍓', 3, 1),
('热带水果', '🍍', 4, 1),
('柑橘橙柚', '🍊', 5, 1),
('瓜类甜果', '🍈', 6, 1);

-- 商品（图片使用公开可访问的水果图片，前端加载失败会自动显示兜底色块+emoji）
INSERT INTO t_product (name, category_id, image, description, price, original_price, stock, sales, hot, status) VALUES
('海南麒麟西瓜 整瓜约4-5斤', 1, 'https://images.unsplash.com/photo-1587049352846-4a222e784d38', '皮薄瓤脆，清甜多汁，夏日消暑首选，产地直发', 29.90, 39.90, 200, 356, 1, 1),
('山东红富士苹果 5斤装', 1, 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6', '脆甜多汁，果香浓郁，现摘现发，家庭常备', 25.80, 32.80, 300, 512, 1, 1),
('进口车厘子 2斤装 JJ级', 2, 'https://images.unsplash.com/photo-1559181567-c3190ca9959b', '果大肉厚，脆甜爆汁，冷链直达新鲜到家', 89.00, 128.00, 80, 203, 1, 1),
('新西兰奇异果 金果6粒', 2, 'https://images.unsplash.com/photo-1618897996318-5a901fa6ca71', '维C小金矿，酸甜爽口，软糯香甜', 39.90, 49.90, 150, 189, 1, 1),
('丹东99草莓 1.5斤装', 3, 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6', '奶香浓郁，入口即化，现摘现发当日达', 49.90, 59.90, 120, 428, 1, 1),
('云南蓝莓 125g*4盒', 3, 'https://images.unsplash.com/photo-1498557850523-fd3d118b962e', '花青素满满，颗颗饱满，酸甜爆浆', 45.00, 55.00, 100, 267, 1, 1),
('海南金钻凤梨 1个装', 4, 'https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2', '无需泡盐水，削皮即食，香甜无渣', 19.90, 26.90, 180, 342, 1, 1),
('泰国金枕榴莲 1个约2-3斤', 4, 'https://images.unsplash.com/photo-1587132137056-bfbf0166836e', '树上熟，果肉金黄，软糯香甜，浓郁醇厚', 99.00, 139.00, 40, 96, 1, 1),
('赣南脐橙 5斤装', 5, 'https://images.unsplash.com/photo-1610832958506-aa56368176cf', '皮薄肉厚，九分甜一分酸，维C满满', 28.80, 36.80, 260, 487, 1, 1),
('沙糖桔 3斤装', 5, 'https://images.unsplash.com/photo-1547514701-42782101795e', '皮薄易剥，蜜甜化渣，一口一个停不下来', 22.90, 29.90, 220, 398, 1, 1),
('新疆哈密瓜 1个装', 6, 'https://images.unsplash.com/photo-1561444569-d5b50f4379b9', '脆甜爽口，瓜香四溢，现摘现发', 24.90, 32.90, 160, 245, 0, 1),
('山东羊角蜜甜瓜 3斤装', 6, 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e', '一口爆汁，清甜爽脆，香甜如蜜', 32.80, 42.80, 140, 178, 0, 1),
('智利青提 2斤装', 2, 'https://images.unsplash.com/photo-1537640538966-79f369143f8f', '果粒饱满，脆甜无籽，冷藏更佳', 36.80, 46.80, 130, 156, 0, 1),
('台农芒果 5斤装', 4, 'https://images.unsplash.com/photo-1553279768-865429fa0078', '果肉细腻，香甜浓郁，无纤维化渣', 33.80, 43.80, 170, 289, 0, 1);

-- 轮播图
INSERT INTO t_banner (image, title, link_url, sort, status) VALUES
('https://images.unsplash.com/photo-1610832958506-aa56368176cf', '鲜果狂欢 低至5折', '/products', 1, 1),
('https://images.unsplash.com/photo-1464965911861-746a04b4bca6', '当季草莓 现摘现发', '/products?categoryId=3', 2, 1),
('https://images.unsplash.com/photo-1559181567-c3190ca9959b', '进口车厘子 冷链直达', '/products?categoryId=2', 3, 1);
