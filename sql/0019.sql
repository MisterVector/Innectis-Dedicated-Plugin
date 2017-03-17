INSERT INTO `version` (`name`,`version`) VALUES ('database', 19) ON DUPLICATE KEY UPDATE `version`=19;

ALTER TABLE `fort` 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `fortid`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `radius` `radius` int(11)   NOT NULL after `locz`, 
	ADD KEY `world`(`world`), COMMENT='';

UPDATE fort SET world='world' WHERE world='';