INSERT INTO version (name,version) VALUES ('database', 93) ON DUPLICATE KEY UPDATE version=93;


ALTER TABLE `otakucraft`.`players` CHANGE COLUMN `backpack` `backpack` TEXT NULL DEFAULT NULL  ;