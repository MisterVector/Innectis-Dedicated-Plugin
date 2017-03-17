ALTER TABLE `innectis_db`.`players` 
ADD COLUMN `last_version` VARCHAR(45) NOT NULL DEFAULT 'unknown' AFTER `backpack`;

INSERT INTO version (name,version) VALUES ('database', 169) ON DUPLICATE KEY UPDATE version = 169;