INSERT INTO `version` (`name`,`version`) VALUES ('database', 30) ON DUPLICATE KEY UPDATE `version`=30;


ALTER TABLE `otakucraft`.`item` ADD COLUMN `discription` VARCHAR(45) NOT NULL DEFAULT 'none'  AFTER `name` ;

ALTER TABLE `otakucraft`.`item` DROP COLUMN `sellstack` , DROP COLUMN `buystack` , DROP COLUMN `forsale` ,
 CHANGE COLUMN `buyprize` `buyprice` INT(11) NOT NULL DEFAULT '0'  ,
CHANGE COLUMN `buypointprize` `buypointprice` INT(11) NOT NULL DEFAULT '0'  , 
CHANGE COLUMN `sellprize` `sellprice` INT(11) NOT NULL DEFAULT '0'  ;

