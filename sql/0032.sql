INSERT INTO `version` (`name`,`version`) VALUES ('database', 32) ON DUPLICATE KEY UPDATE `version`=32;

ALTER TABLE `players` 
	ADD COLUMN `referralpoints` int(11)   NOT NULL after `pvppoints`, 
	CHANGE `balance` `balance` int(11)   NOT NULL DEFAULT '0' after `referralpoints`, COMMENT='';

UPDATE players SET pvppoints = (SELECT points FROM pvp_points WHERE username = players.name LIMIT 1);

DROP TABLE `pvp_points`; 

CREATE TABLE `referral_list`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`referred` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`username`,`referred`) , 
	UNIQUE KEY `referred`(`referred`) , 
	KEY `username`(`username`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
