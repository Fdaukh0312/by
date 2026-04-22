-- 创建数据库
CREATE DATABASE IF NOT EXISTS kedi_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kedi_shop;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role INT DEFAULT 1 COMMENT '角色: 0-管理员, 1-普通用户',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(255) COMMENT '商品图片',
    stock INT DEFAULT 0 COMMENT '库存',
    category_id BIGINT COMMENT '分类ID',
    status INT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_name (name),
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status INT DEFAULT 1 COMMENT '状态: 1-待支付, 2-待发货, 3-待收货, 4-已完成, 5-已取消',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_no (order_no),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255) COMMENT '商品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL COMMENT '购买数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 插入管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt9FhwG', '管理员', 0, 1);

-- 插入商品分类
INSERT INTO category (name, sort) VALUES
('厨房用品', 1),
('清洁用品', 2),
('个人护理', 3),
('家居日用', 4),
('纸品湿巾', 5),
('衣物洗护', 6);

-- 插入商品数据
INSERT INTO product (name, description, price, image, stock, category_id, status) VALUES
-- 厨房用品 (8个)
('不粘锅炒锅 30cm', '优质不粘涂层，少油烹饪，清洗方便，适合家庭日常使用', 89.00, 'https://loremflickr.com/400/400/fryingpan,kitchen?lock=1', 100, 1, 1),
('不锈钢汤锅 24cm', '食品级不锈钢材质，导热均匀，耐用不易生锈', 129.00, 'https://loremflickr.com/400/400/souppot,pot?lock=2', 80, 1, 1),
('菜板砧板 大号', '抗菌防霉竹木菜板，双面可用，不伤刀刃', 39.00, 'https://loremflickr.com/400/400/cuttingboard,wood?lock=3', 150, 1, 1),
('保鲜盒套装 5件套', '玻璃保鲜盒，耐高温可微波，防漏密封', 68.00, 'https://loremflickr.com/400/400/glass,container?lock=4', 120, 1, 1),
('硅胶隔热手套', '耐高温硅胶材质，防滑耐磨，保护双手', 25.00, 'https://loremflickr.com/400/400/kitchen,oven?lock=5', 200, 1, 1),
('真空保温饭盒 1.5L', '双层真空保温，长效锁温，带筷子勺子', 58.00, 'https://loremflickr.com/400/400/lunchbox,meal?lock=6', 90, 1, 1),
('陶瓷调料罐套装', '精美陶瓷材质，防潮防虫，三格分装', 36.00, 'https://loremflickr.com/400/400/spicejar,kitchen?lock=7', 130, 1, 1),
('电动多功能切菜器', '粗丝细丝切片磨泥一机搞定，省时省力', 78.00, 'https://loremflickr.com/400/400/kitchen,chopper?lock=8', 60, 1, 1),

-- 清洁用品 (8个)
('地板清洁剂 1L', '高效清洁配方，去污杀菌，清香不刺鼻', 25.00, 'https://loremflickr.com/400/400/cleaning,detergent?lock=9', 200, 2, 1),
('玻璃清洁剂 500ml', '专业玻璃擦拭，不留水痕，透亮如新', 18.00, 'https://loremflickr.com/400/400/glass,cleaner?lock=10', 180, 2, 1),
('厨房重油污清洁剂', '强力去油配方，轻松去除灶台油烟机油渍', 32.00, 'https://loremflickr.com/400/400/kitchen,grease?lock=11', 90, 2, 1),
('抗菌洁厕灵 500ml', '强效去垢，抗菌除臭，马桶专用', 22.00, 'https://loremflickr.com/400/400/toilet,cleaner?lock=12', 160, 2, 1),
('多效地板清洁片 30片', '一片溶解快速清洁，拖地除菌除螨', 29.00, 'https://loremflickr.com/400/400/cleaning,spray?lock=13', 220, 2, 1),
('魔术擦洗碗海绵 20片', '强力去污无需清洁剂，清洗各类餐具', 15.00, 'https://loremflickr.com/400/400/sponge,dish?lock=14', 300, 2, 1),
('管道疏通剂 500g', '溶解毛发油脂，快速疏通不伤管道', 35.00, 'https://loremflickr.com/400/400/plumbing,drain?lock=15', 100, 2, 1),
('不锈钢清洁膏 300g', '去除烧黑烧黄水垢，一擦即亮', 26.00, 'https://loremflickr.com/400/400/metal,clean?lock=16', 140, 2, 1),

-- 个人护理 (8个)
('洗发水 750ml', '滋养修护配方，深层清洁，改善干枯毛躁', 49.00, 'https://loremflickr.com/400/400/shampoo,bottle?lock=17', 250, 3, 1),
('沐浴露 1L', '温和洁净，保湿滋润，持久留香', 45.00, 'https://loremflickr.com/400/400/shower,gel?lock=18', 200, 3, 1),
('牙膏 超值装 3支', '防蛀固齿，清新口气，家庭装更划算', 35.00, 'https://loremflickr.com/400/400/toothpaste,dental?lock=19', 300, 3, 1),
('护发素 500ml', '深层滋养，柔顺秀发，减少分叉断裂', 42.00, 'https://loremflickr.com/400/400/conditioner,haircare?lock=20', 180, 3, 1),
('洗面奶 150ml', '温和控油深层清洁，泡沫丰富洗后不紧绷', 38.00, 'https://loremflickr.com/400/400/facewash,skincare?lock=21', 200, 3, 1),
('润肤乳 300ml', '保湿补水滋润肌肤，全身可用不黏腻', 55.00, 'https://loremflickr.com/400/400/lotion,moisturizer?lock=22', 150, 3, 1),
('洗手液 500ml', '抑菌清洁，温和护手，清爽不干涩', 22.00, 'https://loremflickr.com/400/400/hand,sanitizer?lock=23', 280, 3, 1),
('指甲剪套装 8件套', '不锈钢精工打造，圆润不伤甲，便携收纳', 28.00, 'https://loremflickr.com/400/400/nail,clipper?lock=24', 160, 3, 1),

-- 家居日用 (8个)
('垃圾袋 加厚 100只', '断点式设计，强韧不易破，容量大', 28.00, 'https://loremflickr.com/400/400/garbage,bag?lock=25', 300, 4, 1),
('抹布 10条装', '超强吸水，不掉毛絮，多色随机', 19.00, 'https://loremflickr.com/400/400/cloth,cleaning?lock=26', 280, 4, 1),
('拖把 旋转拖', '免手洗拧水，强力清洁，干湿两用', 65.00, 'https://loremflickr.com/400/400/mop,floor?lock=27', 80, 4, 1),
('收纳箱 大号', '透明可视，分类收纳，节省空间', 48.00, 'https://loremflickr.com/400/400/storage,box?lock=28', 100, 4, 1),
('静电除尘掸 40cm', '静电吸附灰尘毛发，不扬尘可水洗', 18.00, 'https://loremflickr.com/400/400/duster,dust?lock=29', 200, 4, 1),
('加厚脸盆套装 3个', '优质PP材质，耐用不易变形，大小一套', 32.00, 'https://loremflickr.com/400/400/basin,plastic?lock=30', 120, 4, 1),
('真空压缩袋 6件套', '节省衣柜空间，防潮防虫防霉', 39.00, 'https://loremflickr.com/400/400/storage,vacuum?lock=31', 90, 4, 1),
('雨伞全自动 加大加固', '一键开收，防风加固伞骨，遮阳挡雨', 45.00, 'https://loremflickr.com/400/400/umbrella,rain?lock=32', 110, 4, 1),

-- 纸品湿巾 (8个)
('抽纸 3层 130抽*24包', '柔软厚实，湿水不易破，整箱批发', 42.00, 'https://loremflickr.com/400/400/tissue,paper?lock=33', 400, 5, 1),
('卷纸 4层 140g*30卷', '原生木浆，柔韧舒适，经济实惠', 38.00, 'https://loremflickr.com/400/400/toilet,paper?lock=34', 350, 5, 1),
('湿巾 80片*3包', 'EDI纯水配方，温和清洁，随手擦拭', 25.00, 'https://loremflickr.com/400/400/wet,wipe?lock=35', 220, 5, 1),
('厨房纸巾 2卷', '吸油锁水，不易烂，一纸多用', 18.00, 'https://loremflickr.com/400/400/kitchen,towel?lock=36', 180, 5, 1),
('酒精湿巾 50片*3包', '75%浓度有效杀菌，手机眼镜均可擦拭', 20.00, 'https://loremflickr.com/400/400/alcohol,wipe?lock=37', 250, 5, 1),
('婴儿湿巾 80片*4包', 'EDI纯水无添加，弱酸性配方呵护宝宝肌肤', 35.00, 'https://loremflickr.com/400/400/baby,wipe?lock=38', 180, 5, 1),
('湿厕纸 40片*6包', '可冲散不堵马桶，洁净舒适温和亲肤', 32.00, 'https://loremflickr.com/400/400/wash,wipe?lock=39', 160, 5, 1),
('手帕纸 3层 30包', '柔滑细腻，随手便携，迷你小包', 26.00, 'https://loremflickr.com/400/400/tissue,napkin?lock=40', 300, 5, 1),

-- 衣物洗护 (8个)
('洗衣液 3kg', '深层清洁，护色护衣，温和不刺激', 55.00, 'https://loremflickr.com/400/400/laundry,detergent?lock=41', 150, 6, 1),
('柔顺剂 2L', '柔顺护衣，防静电，清香怡人', 38.00, 'https://loremflickr.com/400/400/fabric,softener?lock=42', 130, 6, 1),
('衣领净 500ml', '强力渗透，轻松去除领口袖口顽固污渍', 26.00, 'https://loremflickr.com/400/400/laundry,stain?lock=43', 120, 6, 1),
('洗衣皂 5块装', '天然成分，温和去渍，护色增艳', 20.00, 'https://loremflickr.com/400/400/soap,laundry?lock=44', 180, 6, 1),
('洗衣粉 3kg', '强力去污，低泡易漂，清香持久', 32.00, 'https://loremflickr.com/400/400/washing,powder?lock=45', 200, 6, 1),
('洗衣留香珠 200g', '微胶囊留香技术，洗后持久芳香', 45.00, 'https://loremflickr.com/400/400/perfume,fresh?lock=46', 100, 6, 1),
('防染色片 30片', '混洗不串色，深浅衣物一起洗无忧', 18.00, 'https://loremflickr.com/400/400/laundry,color?lock=47', 220, 6, 1),
('洗衣网袋 3个装', '保护衣物不变形，细腻网面防止起球', 22.00, 'https://loremflickr.com/400/400/laundry,bag?lock=48', 160, 6, 1);
