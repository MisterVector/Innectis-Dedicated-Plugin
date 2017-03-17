INSERT INTO version (name,version) VALUES ('database', 77) ON DUPLICATE KEY UPDATE version=77;

CREATE TABLE `player_channels`(
	`username` varchar(50) COLLATE latin1_swedish_ci NOT NULL  , 
	`channel` varchar(20) COLLATE latin1_swedish_ci NOT NULL  , 
	`num` int(11) NOT NULL  , 
	PRIMARY KEY (`username`,`channel`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
