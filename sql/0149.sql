CREATE TABLE `lot_safelist`(
	`lotid` int(11) NULL  , 
	`username` varchar(60) COLLATE utf8_general_ci NULL  
) ENGINE=MyISAM DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 149) ON DUPLICATE KEY UPDATE version = 149;