ALTER TABLE `innectis_db`.`players` 
ADD COLUMN `refer_bonus` DOUBLE NOT NULL DEFAULT 0 AFTER `last_version`,
ADD COLUMN `refer_type` DOUBLE NOT NULL DEFAULT 0 AFTER `refer_bonus`,
ADD COLUMN `refer_id` VARCHAR(180) NULL AFTER `refer_type`;

INSERT INTO version (name,version) VALUES ('database', 170) ON DUPLICATE KEY UPDATE version = 170;