INSERT INTO version (name,version) VALUES ('database', 101) ON DUPLICATE KEY UPDATE version=101;

DROP TABLE `banned_ip_logger`; 

CREATE TABLE `banned_ip_players`(
	`ID` int(11) NOT NULL  auto_increment , 
	`iplist` text COLLATE latin1_swedish_ci NULL  , 
	`userlist` text COLLATE latin1_swedish_ci NULL  , 
	`banned_by` varchar(15) COLLATE latin1_swedish_ci NULL  , 
	`banned_time` timestamp NULL  , 
	`duration_ticks` bigint(20) NULL  , 
	`joinban` tinyint(1) NULL  , 
	PRIMARY KEY (`ID`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

DELETE FROM `banned_players` WHERE `is_ipbanned` = '1';

ALTER TABLE `banned_players` 
	ADD COLUMN `ID` int(11)   NOT NULL auto_increment first, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `ID`, 
	CHANGE `banned_by` `banned_by` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by`, 
	DROP COLUMN `is_ipbanned`, 
	ADD UNIQUE KEY `ID`(`ID`), 
	DROP KEY `PRIMARY`, COMMENT='';