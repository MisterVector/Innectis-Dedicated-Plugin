INSERT INTO `version` (`name`,`version`) VALUES ('database', 24) ON DUPLICATE KEY UPDATE `version`=24;

ALTER TABLE `lots` 
	CHANGE `lastedit` `lastedit` bigint(20)   NOT NULL after `creator`, COMMENT='';
