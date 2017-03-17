INSERT INTO `version` (`name`,`version`) VALUES ('database', 26) ON DUPLICATE KEY UPDATE `version`=26;

ALTER TABLE `lots` 
	CHANGE `creator` `creator` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `flags`, 
	CHANGE `lastedit` `lastedit` bigint(20)   NOT NULL after `creator`, 
	ADD COLUMN `hidden` tinyint(1)   NOT NULL after `lastedit`, 
	DROP COLUMN `parent`, 
	DROP KEY `parent`, COMMENT='';