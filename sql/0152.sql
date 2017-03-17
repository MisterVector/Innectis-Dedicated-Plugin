CREATE TABLE `lot_respawns`(
	`name` varchar(60) COLLATE utf8_general_ci NULL  , 
	`personalid` int(11) NULL  
) ENGINE=InnoDB DEFAULT CHARSET='utf8';


INSERT INTO version (name,version) VALUES ('database', 152) ON DUPLICATE KEY UPDATE version = 152;