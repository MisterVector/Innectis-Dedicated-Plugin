INSERT INTO `version` (`name`,`version`) VALUES ('database', 10) ON DUPLICATE KEY UPDATE `version`=10;

ALTER TABLE `chests_members` 
	ADD KEY `chestid`(`chestid`), 
	DROP KEY `PRIMARY`, COMMENT='';

ALTER TABLE `doors_members` 
	ADD KEY `doorid`(`doorid`), 
	DROP KEY `PRIMARY`, COMMENT='';