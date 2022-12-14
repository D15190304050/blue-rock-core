DROP TABLE IF EXISTS `file_metadata`;
CREATE TABLE `file_metadata`
(
    `id`           BIGINT                                           NOT NULL AUTO_INCREMENT COMMENT 'ID of the file.',
    `bucket_name`  VARCHAR(200) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Name of the bucket that stores the file.',
    `object_name`  VARCHAR(100) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Name of the object in minio.',
    `file_name`    VARCHAR(100) CHARSET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Name of the file (including file extension, excluding the directory prefix).',
    `url`          VARCHAR(300) CHARSET utf8mb4 COLLATE utf8mb4_bin COMMENT 'Access URL of the file.',
    `directory_id` BIGINT                                           NOT NULL COMMENT 'ID of the directory that directly contains the file.',
    `user_id`      BIGINT                                           NOT NULL COMMENT 'ID of the user who possesses the file.',
    `state`        INT                                              NOT NULL DEFAULT 0 COMMENT 'State of the file: -1 - Uploading unfinished; 0 - in the user\'s space; 1 - in the user\'s trash; 2 - should be deleted, but not deleted in MinIO yet.',
    PRIMARY KEY (`id`),
    KEY `idx_directory_id` (`directory_id`),
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4
    COLLATE utf8mb4_bin
    COMMENT 'Metadata of files.';