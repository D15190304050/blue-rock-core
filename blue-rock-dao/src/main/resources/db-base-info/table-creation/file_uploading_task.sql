DROP TABLE IF EXISTS `file_uploading_task`;
CREATE TABLE `file_uploading_task`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'ID.',
    `metadata_id`   BIGINT   NOT NULL COMMENT 'ID of the correspond file metadata.',
    `chunk_count`   BIGINT   NOT NULL COMMENT 'Number of chunks to upload.',
    `user_id`       BIGINT   NOT NULL COMMENT 'ID of the user that uploads the file.',
    `creation_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the file uploading task.',
    `update_time`   DATETIME NOT NULL DEFAULT NOW() ON UPDATE NOW() COMMENT 'Update time of the directory.',
    `state`         INT      NOT NULL DEFAULT 0 COMMENT 'State of the file uploading task: 0 - Not started; 1 - Uploading; 2 - Paused; 3 - Success; 4 - Failed; 5 - Deleted.',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Tracing of file uploading tasks.';