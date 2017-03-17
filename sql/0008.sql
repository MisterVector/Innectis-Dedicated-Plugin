INSERT INTO `version` (`name`,`version`) VALUES ('database', 8) ON DUPLICATE KEY UPDATE `version`=8;

CREATE TABLE `deaths`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`time` bigint(20) NOT NULL  , 
	PRIMARY KEY (`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
