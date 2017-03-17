ALTER TABLE `players` 
	DROP COLUMN `ignorelist`, COMMENT='';

CREATE TABLE `players_ignored`(
	`player` varchar(60) COLLATE utf8_general_ci NULL  , 
	`ignored_player` varchar(60) COLLATE utf8_general_ci NULL  
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 159) ON DUPLICATE KEY UPDATE version = 159;