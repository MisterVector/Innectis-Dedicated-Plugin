INSERT INTO `version` (`name`,`version`) VALUES ('database', 39) ON DUPLICATE KEY UPDATE `version`=39;

ALTER TABLE `players` 
	ADD COLUMN `votepoints` int(11)   NOT NULL DEFAULT '0' after `referralpoints`, 
	CHANGE `balance` `balance` int(11)   NOT NULL DEFAULT '0' after `votepoints`, COMMENT='';

CREATE TABLE `vote_log`(
	`time` datetime NOT NULL  , 
	`service` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`ip` varchar(30) COLLATE latin1_swedish_ci NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	KEY `ip`(`ip`) , 
	KEY `username`(`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
