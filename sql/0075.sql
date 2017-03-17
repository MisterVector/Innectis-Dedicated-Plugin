INSERT INTO version (name,version) VALUES ('database', 75) ON DUPLICATE KEY UPDATE version=75;

ALTER TABLE `homes` 
	ADD COLUMN `homeid` int(11)   NOT NULL after `username`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `homeid`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`username`,`homeid`), COMMENT='';