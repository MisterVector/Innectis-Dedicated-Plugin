INSERT INTO `version` (`name`,`version`) VALUES ('database', 14) ON DUPLICATE KEY UPDATE `version`=14;

ALTER TABLE `kickedplayers` 
	CHANGE `till` `till` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `name`, COMMENT='';

DROP TABLE `lot_requests`; 