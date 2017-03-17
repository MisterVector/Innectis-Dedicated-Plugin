INSERT INTO version (name,version) VALUES ('database', 105) ON DUPLICATE KEY UPDATE version=105;

ALTER TABLE `shop_items` 
	CHANGE `id` `id` int(11)   NOT NULL auto_increment first, 
	ADD COLUMN `itemid` int(11)   NOT NULL after `id`, 
	ADD COLUMN `itemdata` int(11)   NOT NULL after `itemid`, 
	CHANGE `buycost` `buycost` int(11)   NOT NULL after `itemdata`, 
	CHANGE `sellcost` `sellcost` int(11)   NOT NULL after `buycost`, 
	CHANGE `amount` `amount` int(11)   NOT NULL DEFAULT '0' after `sellcost`, 
	DROP COLUMN `data`, 
	ADD PRIMARY KEY(`id`), COMMENT='';