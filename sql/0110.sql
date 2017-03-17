INSERT INTO version (name,version) VALUES ('database', 110) ON DUPLICATE KEY UPDATE version=110;

ALTER TABLE `innectis_db`.`player_inventory` ADD COLUMN `health` INT(11) NOT NULL DEFAULT '20'  AFTER `content` , ADD COLUMN `hunger` INT(11) NOT NULL DEFAULT '20'  AFTER `health` ;