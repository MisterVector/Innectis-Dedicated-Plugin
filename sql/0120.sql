INSERT INTO version (name,version) VALUES ('database', 120) ON DUPLICATE KEY UPDATE version = 120;

ALTER TABLE `homes` 
	ADD COLUMN `ID` int(11)   NOT NULL auto_increment first, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `ID`, 
	CHANGE `homeid` `homeid` int(11)   NOT NULL after `username`, 
	CHANGE `homename` `homename` varchar(60)  COLLATE latin1_swedish_ci NULL after `homeid`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `homename`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, 
	ADD PRIMARY KEY(`ID`), COMMENT='';
