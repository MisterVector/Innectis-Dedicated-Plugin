INSERT INTO `version` (`name`,`version`) VALUES ('database', 40) ON DUPLICATE KEY UPDATE `version`=40;

ALTER TABLE `vote_log` 
	CHANGE `time` `time` varchar(30)  COLLATE latin1_swedish_ci NOT NULL first, COMMENT='';