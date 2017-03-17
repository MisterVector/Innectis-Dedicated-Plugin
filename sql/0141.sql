ALTER TABLE `innectis_db`.`player_inventory` ADD COLUMN `potioneffects` VARCHAR(45) NULL  AFTER `hunger` ;

INSERT INTO version (name,version) VALUES ('database', 141) ON DUPLICATE KEY UPDATE version = 141;