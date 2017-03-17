INSERT INTO `version` (`name`,`version`) VALUES ('database', 11) ON DUPLICATE KEY UPDATE `version`=11;

CREATE TABLE `waypoints`(
	`waypointid` int(11) unsigned NOT NULL  auto_increment , 
	`owner` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`world` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`locx` int(11) NOT NULL  , 
	`locy` int(11) NOT NULL  , 
	`locz` int(11) NOT NULL  , 
	`tlocx` int(11) NOT NULL  , 
	`tlocy` int(11) NOT NULL  , 
	`tlocz` int(11) NOT NULL  , 
	PRIMARY KEY (`waypointid`) , 
	UNIQUE KEY `loc`(`world`,`locx`,`locy`,`locz`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
