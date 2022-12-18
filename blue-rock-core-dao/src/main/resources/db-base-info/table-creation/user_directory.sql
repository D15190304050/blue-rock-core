DROP TABLE IF EXISTS `user_directory`;
CREATE TABLE `user_directory`
(
    `id`            BIGINT                                           NOT NULL AUTO_INCREMENT COMMENT 'ID of the directory.',
    `parent_id`     BIGINT                                           NOT NULL DEFAULT 0 COMMENT 'ID of the parent directory, null for top level directories.',
    `user_id`       BIGINT                                           NOT NULL COMMENT 'User ID.',
    `name`          VARCHAR(50) CHARSET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'Name of the directory.',
    `path`          VARCHAR(400) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Quick access of the directory path, end with [/].',
    `creation_time` DATETIME                                         NOT NULL DEFAULT NOW() COMMENT 'Creation time of the directory.',
    `update_time`   DATETIME                                         NOT NULL DEFAULT NOW() ON UPDATE NOW() COMMENT 'Update time of the directory.',
    `state`         INT                                              NOT NULL DEFAULT 0 COMMENT 'State of the directory: 0 - in the user\'s space; 1 - in the user\'s trash; 2 - should be deleted, but not yet.',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Relationships between users and directories.';