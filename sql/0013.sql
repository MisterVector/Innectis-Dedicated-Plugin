INSERT INTO `version` (`name`,`version`) VALUES ('database', 13) ON DUPLICATE KEY UPDATE `version`=13;

CREATE TABLE `block_breaks`(
	`time` datetime NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`blockid` int(11) NOT NULL  , 
	`x` int(11) NOT NULL  , 
	`y` int(11) NOT NULL  , 
	`z` int(11) NOT NULL  , 
	KEY `time`(`time`) , 
	KEY `username`(`username`) , 
	KEY `blockid`(`blockid`) , 
	KEY `coords`(`x`,`y`,`z`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
