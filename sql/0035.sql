INSERT INTO `version` (`name`,`version`) VALUES ('database', 35) ON DUPLICATE KEY UPDATE `version`=35;

DROP TABLE `blocklog`; 

DROP TABLE `chatlog`; 

CREATE TABLE `lot_messages`(
	`lotid` int(11) NOT NULL  , 
	`type` tinyint(4) NOT NULL  , 
	`message` varchar(255) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`lotid`, `type`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';