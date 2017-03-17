CREATE TABLE `trash_items`(
	`id` int(11) NOT NULL  auto_increment , 
	`typeid` int(11) NOT NULL  , 
	`data` int(11) NOT NULL  , 
	`amount` int(11) NOT NULL  , 
	`itemdata` longblob NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=MyISAM DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 164) ON DUPLICATE KEY UPDATE version = 164;