INSERT INTO version (name,version) VALUES ('database', 56) ON DUPLICATE KEY UPDATE version=56;


CREATE TABLE `banned_ip_logger`(
	`username` varchar(45) COLLATE latin1_swedish_ci NOT NULL  , 
	`ip_address` varchar(45) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`username`,`ip_address`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


ALTER TABLE `banned_players` 
	ADD COLUMN `username` varchar(45)  COLLATE latin1_swedish_ci NOT NULL first, 
	ADD COLUMN `banned_by` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	ADD COLUMN `is_ipbanned` tinyint(1)   NOT NULL after `banned_by`, 
	CHANGE `end_date` `end_date` datetime   NULL after `is_ipbanned`, 
	DROP COLUMN `ban_id`, 
	DROP COLUMN `name`, 
	DROP COLUMN `date_lifted`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`username`), COMMENT='';