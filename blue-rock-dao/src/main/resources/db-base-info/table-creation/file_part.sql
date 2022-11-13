DROP TABLE IF EXISTS file_chunk;
CREATE TABLE file_chunk
(
    `id`             BIGINT                                           NOT NULL AUTO_INCREMENT COMMENT 'ID.',
    `task_id`        BIGINT                                           NOT NULL COMMENT 'ID of the file uploading progress.',
    `object_name`    VARCHAR(200) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Name of the file on the server.',
    `byte_count`     BIGINT                                           NOT NULL COMMENT 'Number of bytes in this file part.',
    `uploading_time` DATETIME                                         NOT NULL DEFAULT NOW() ON UPDATE NOW() COMMENT 'Updating time of this part.',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'File chunk of a file uploading task.'