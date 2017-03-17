ALTER TABLE `innectis_db`.`lots` ADD COLUMN `last_member_edit` VARCHAR(45) NULL  AFTER `last_owner_edit` , CHANGE COLUMN `lastedit` `last_owner_edit` BIGINT(20) NOT NULL  ;

INSERT INTO version (name,version) VALUES ('database', 133) ON DUPLICATE KEY UPDATE version = 133;