INSERT INTO `version` (`name`,`version`) VALUES ('database', 33) ON DUPLICATE KEY UPDATE `version`=33;



ALTER TABLE `otakucraft`.`item` DROP COLUMN `maxstock` , DROP COLUMN `respawnstock` , DROP COLUMN `stock` , DROP COLUMN `buypointprice` , DROP COLUMN `discription` , DROP COLUMN `name` , ADD COLUMN `pointsonly` INT(11) NOT NULL DEFAULT '0'  AFTER `sellprice` 

, DROP INDEX `name_UNIQUE` ;

