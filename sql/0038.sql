INSERT INTO `version` (`name`,`version`) VALUES ('database', 38) ON DUPLICATE KEY UPDATE `version`=38;

CREATE TABLE `referral_forum_cache`(
	`userid` int(11) NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`mcname` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`referrer` int(11) NOT NULL  , 
	`referrer_username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`referrer_mcname` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`userid`) , 
	UNIQUE KEY `username`(`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

ALTER TABLE `stored_inventory` 
	CHANGE `idstored_inventory` `idstored_inventory` int(11)   NOT NULL auto_increment first, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `idstored_inventory`, 
	CHANGE `name` `name` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	CHANGE `inventory` `inventory` blob   NOT NULL after `name`, COMMENT='';

DELETE FROM `referral_list`;

UPDATE `players` SET `referralpoints` = 0;