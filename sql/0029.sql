INSERT INTO `version` (`name`,`version`) VALUES ('database', 29) ON DUPLICATE KEY UPDATE `version`=29;

CREATE TABLE `held_items`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`id` int(11) NOT NULL  , 
	`amount` int(11) NOT NULL  , 
	`durability` int(11) NOT NULL  , 
	`data` int(11) NOT NULL  , 
	KEY `username`(`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

CREATE TABLE `saved_inventory`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`slot` int(11) NOT NULL  , 
	`id` int(11) NOT NULL  , 
	`amount` int(11) NOT NULL  , 
	`durability` int(11) NOT NULL  , 
	`data` int(11) NOT NULL  
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
