INSERT INTO `version` (`name`,`version`) VALUES ('database', 12) ON DUPLICATE KEY UPDATE `version`=12;

CREATE TABLE `pvp_kills`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`victim` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`time` datetime NOT NULL  , 
	KEY `username`(`username`) , 
	KEY `victim`(`victim`) , 
	KEY `time`(`time`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `pvp_opt`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`enabled` tinyint(1) NOT NULL  DEFAULT '0' , 
	PRIMARY KEY (`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `pvp_points`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`points` int(11) NOT NULL  , 
	PRIMARY KEY (`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


ALTER TABLE `waypoints` 
	ADD COLUMN `tyaw` int(11)   NOT NULL after `tlocz`, COMMENT='';