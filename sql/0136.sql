ALTER TABLE `player_inventory` 
	CHANGE `health` `health` double   NOT NULL DEFAULT '20' after `bagid`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`inventoryid`,`health`), COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 136) ON DUPLICATE KEY UPDATE version = 136;