DROP TABLE IF EXISTS `user_operation_log`;
CREATE TABLE `user_operation_log`
(
    `id`                BIGINT                                           NOT NULL AUTO_INCREMENT COMMENT 'ID.',
    `operation_type_id` INT                                              NOT NULL COMMENT 'ID of operation type, see table `operation_type` for more information.',
    `user_id`           BIGINT                                           NOT NULL COMMENT 'User ID.',
    `detail`            VARCHAR(300) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Details of the operation.',
    PRIMARY KEY (`id`),
    KEY `idx_operation_type_id` (`operation_type_id`),
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Log of user operations.';