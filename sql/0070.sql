INSERT INTO version (name,version) VALUES ('database', 70) ON DUPLICATE KEY UPDATE version=70;

DROP TABLE `ip_conlog`; 
DROP TABLE `ip_gen`; 

CREATE TABLE `ban_whitelist`(
	`name` varchar(45) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`name`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


CREATE TABLE `ip_log`(
	`name` varchar(45) COLLATE latin1_swedish_ci NOT NULL  , 
	`ip` varchar(45) COLLATE latin1_swedish_ci NULL  , 
	`logtime` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP , 
	PRIMARY KEY (`name`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
