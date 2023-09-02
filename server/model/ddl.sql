create
database if not exists gm_fs;
use gm_fs;
-- 用户信息
drop table if exists user_info;
CREATE TABLE `user_info`
(
    `user_id`      BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'user id',
    `user_name`    VARCHAR(128)  NOT NULL COMMENT '用户名字',
    `hash_id`      VARCHAR(1024) NOT NULL COMMENT '用户身份标识',
    `email`        VARCHAR(256)  NOT NULL COMMENT '用户邮箱',
    `bi_index`     Longtext NOT NULL COMMENT '用户双向索引链表',
    `root_node_id` BIGINT UNSIGNED NOT NULL COMMENT '用户网盘根节点id',
    `key1`         VARCHAR(1024) NOT NULL COMMENT '用户密钥1',
    `key2`         VARCHAR(1024) NOT NULL COMMENT '用户密钥2',
    `create_time`  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `unique_name`(`user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表';
drop table if exists node;
-- 文件夹节点
CREATE TABLE `node`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键id',
    `node_id`     BIGINT UNSIGNED NOT NULL COMMENT '节点id,分享文件使用,64bit',
    `node_type`   TINYINT UNSIGNED NOT NULL COMMENT '节点类型',
    `name`        VARCHAR(1024) NOT NULL COMMENT '文件名称',
    `is_delete`   INT           NOT NULL COMMENT '是否被删除',
    `create_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX         `idx_node_id`(`node_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '节点表';

drop table if exists node_rel;
-- 文件引用关系
CREATE TABLE `node_rel`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键id',
    `parent_id`   BIGINT UNSIGNED NOT NULL COMMENT '父节点id',
    `child_id`    BIGINT UNSIGNED NOT NULL COMMENT '子节点id',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件节点引用表';

drop table if exists file_info;
-- 文件信息
CREATE TABLE `file_info`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键id',
    `node_id`     BIGINT UNSIGNED NOT NULL COMMENT '节点id,分享文件使用,64bit',
    `file_secret` VARCHAR(1024) NOT NULL COMMENT '文件密钥',
    `is_share`    INT           NOT NULL COMMENT '是否为分享文件',
    `address`     BIGINT UNSIGNED NOT NULL COMMENT '文件内容地址',
    `create_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX         `idx_node_id`(`node_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件节点信息表';


drop table if exists file_index;
-- for linzefu
CREATE TABLE `file_index`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键id',
    `L`           VARCHAR(256) NOT NULL COMMENT '',
    `I_w`         VARCHAR(256) NOT NULL COMMENT '',
    `R_w`         VARCHAR(256) NOT NULL COMMENT '',
    `C_w`         VARCHAR(256) NOT NULL COMMENT '',
    `I_id`        VARCHAR(256) NOT NULL COMMENT '',
    `R_id`        VARCHAR(256) NOT NULL COMMENT '',
    `C_id`        VARCHAR(256) NOT NULL COMMENT '',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX         `idx_L`(`L`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件节点引用表';

drop table if exists share_token;
CREATE TABLE `share_token`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键id',
    `share_token_id` VARCHAR(128) NOT NULL COMMENT '分享令牌编号',
    `L`              VARCHAR(256) NOT NULL COMMENT '',
    `J_id`           VARCHAR(256) NOT NULL COMMENT '',
    `k_id`           VARCHAR(256) NOT NULL COMMENT '',
    `file_id`        VARCHAR(256) NOT NULL COMMENT '',
    `owner_id`       VARCHAR(256) NOT NULL COMMENT '分享名',
    `user_id`        VARCHAR(256) NOT NULL COMMENT '文件名',
    `secret_key`     VARCHAR(256) NOT NULL COMMENT '文件密钥',
    `file_name`       varchar(256) NOT NULL COMMENT '文件名',
    `is_received`      INT          NOT NULL COMMENT '是否被接收，1表示被接收，0表示未被接收',
    `create_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_share`       varchar(10) NOT NULL COMMENT '',
    PRIMARY KEY (`id`),
    INDEX            `idx_L`(`L`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分享令牌表';