INSERT INTO version (name,version) VALUES ('database', 87) ON DUPLICATE KEY UPDATE version=87;

ALTER TABLE `players` 
	ADD COLUMN `shopnotification` tinyint(1)   NULL DEFAULT '1' after `starvation`, COMMENT='';

DROP TABLE `shop_chests`; 
DROP TABLE `shop_contents`; 
DROP TABLE `shop_innectisicontents`; 
DROP TABLE `shop_innectisitems`; 
DROP TABLE `shop_items_official`; 

ALTER TABLE `shop_items` 
	CHANGE `ID` `ID` int(11)   NOT NULL first, 
	CHANGE `data` `data` int(11)   NOT NULL after `ID`, 
	ADD COLUMN `buycost` int(11)   NOT NULL after `data`, 
	ADD COLUMN `sellcost` int(11)   NOT NULL after `buycost`, 
	ADD COLUMN `amount` int(11)   NOT NULL DEFAULT '0' after `sellcost`, 
	DROP COLUMN `owner`, 
	DROP COLUMN `cost`, COMMENT='';
