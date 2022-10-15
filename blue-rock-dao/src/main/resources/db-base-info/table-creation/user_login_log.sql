DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID.',
    `user_id`    BIGINT       NOT NULL COMMENT 'User ID.',
    `login_time` DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Time of login operation.',
    `login_ip`   VARCHAR(150) NOT NULL COMMENT 'IP address of the login operation.',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Log of user logins.';