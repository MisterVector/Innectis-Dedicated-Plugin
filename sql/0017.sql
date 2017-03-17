INSERT INTO `version` (`name`,`version`) VALUES ('database', 17) ON DUPLICATE KEY UPDATE `version`=17;

CREATE TABLE `block_locks`(
	`lockid` int(10) unsigned NOT NULL  auto_increment , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`world` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`locx` int(11) NOT NULL  , 
	`locy` int(11) NOT NULL  , 
	`locz` int(11) NOT NULL  , 
	PRIMARY KEY (`lockid`) , 
	UNIQUE KEY `coords`(`world`,`locx`,`locy`,`locz`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
