INSERT INTO version (name,version) VALUES ('database', 78) ON DUPLICATE KEY UPDATE version=78;

/* Create table in target */
CREATE TABLE `playermail`(
	`ID` int(11) NOT NULL  auto_increment , 
	`toplayer` varchar(16) COLLATE utf8_general_ci NULL  , 
	`fromplayer` varchar(16) COLLATE utf8_general_ci NULL  , 
	`title` varchar(30) COLLATE utf8_general_ci NULL  , 
	`content` varchar(100) COLLATE utf8_general_ci NULL  , 
	`readmail` bit(1) NOT NULL  , 
	PRIMARY KEY (`ID`) 
) ENGINE=MyISAM DEFAULT CHARSET='utf8';
