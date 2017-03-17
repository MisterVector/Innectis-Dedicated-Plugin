CREATE TABLE `chests_members`(
	`chestid` int(11) unsigned NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`chestid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';



CREATE TABLE `version`(
	`name` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`version` int(10) unsigned NOT NULL  , 
	PRIMARY KEY (`name`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

INSERT INTO `version` (`name`,`version`) VALUES ('database', 4) ON DUPLICATE KEY UPDATE `version`=4;

update chests set locked = 1;