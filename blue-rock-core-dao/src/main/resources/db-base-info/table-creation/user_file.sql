DROP TABLE IF EXISTS `user_file`;
CREATE TABLE `user_file`
(
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID.',
    `user_id` BIGINT NOT NULL COMMENT 'User ID.',
    `file_id` BIGINT NOT NULL COMMENT 'File ID.',
    PRIMARY KEY (`id`),
    KEY       `idx_user_id` (`user_id`),
    KEY       `idx_file_id` (`file_id`)
) ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Relationships between users and files.';