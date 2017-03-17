CREATE TABLE `the_end_pot`(
	`player` varchar(60) COLLATE utf8_general_ci NULL  , 
	`value` double NULL  DEFAULT '0' 
) ENGINE=MyISAM DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 156) ON DUPLICATE KEY UPDATE version = 156;