INSERT INTO `version` (`name`,`version`) VALUES ('database', 21) ON DUPLICATE KEY UPDATE `version`=21;

ALTER TABLE `waypoints` 
	ADD COLUMN `tworld` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locz`, 
	CHANGE `tlocx` `tlocx` int(11)   NOT NULL after `tworld`, 
	CHANGE `tlocy` `tlocy` int(11)   NOT NULL after `tlocx`, 
	CHANGE `tlocz` `tlocz` int(11)   NOT NULL after `tlocy`, 
	CHANGE `tyaw` `tyaw` int(11)   NOT NULL after `tlocz`, COMMENT='';

UPDATE `waypoints` SET `tworld` = 'world';