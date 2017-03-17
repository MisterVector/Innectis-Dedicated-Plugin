ALTER TABLE `innectis_db`.`lot_banned` ADD COLUMN `timeout` BIGINT NOT NULL  AFTER `username` ;

INSERT INTO version (name,version) VALUES ('database', 142) ON DUPLICATE KEY UPDATE version = 142;