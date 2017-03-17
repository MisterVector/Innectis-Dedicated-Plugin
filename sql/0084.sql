INSERT INTO version (name,version) VALUES ('database', 84) ON DUPLICATE KEY UPDATE version=84;

ALTER TABLE `homes` 
	ADD COLUMN `homename` varchar(60)  COLLATE latin1_swedish_ci NULL after `homeid`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `homename`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, 
	DROP KEY `PRIMARY`, COMMENT='';