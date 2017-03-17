CREATE TABLE `chest_shop_list`(
	`id` int(11) NOT NULL  auto_increment , 
	`lotid` int(11) NOT NULL  , 
	`name` varchar(100) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
INSERT INTO version (name,version) VALUES ('database', 180) ON DUPLICATE KEY UPDATE version = 180;