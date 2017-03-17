INSERT INTO `version` (`name`,`version`) VALUES ('database', 41) ON DUPLICATE KEY UPDATE `version`=41;

ALTER TABLE `vote_log` 
	ADD COLUMN `service_address` varchar(30)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '0.0.0.0' after `service`, 
	CHANGE `ip` `ip` varchar(30)  COLLATE latin1_swedish_ci NOT NULL after `service_address`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `ip`, COMMENT='';

CREATE TABLE `vote_services`(
	`ip` varchar(30) COLLATE latin1_swedish_ci NOT NULL  , 
	`note` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`ip`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';