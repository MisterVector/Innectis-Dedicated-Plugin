CREATE TABLE `player_mining_stick`(
	`id` int(11) NOT NULL  auto_increment , 
	`player_id` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`settings` double NOT NULL  , 
	`size` int(11) NOT NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

ALTER TABLE `players` 
	DROP COLUMN `stick_size`, COMMENT='';
INSERT INTO version (name,version) VALUES ('database', 178) ON DUPLICATE KEY UPDATE version = 178;