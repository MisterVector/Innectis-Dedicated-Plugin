CREATE TABLE `player_renames`(
	`player_id` varchar(45) COLLATE utf8_general_ci NOT NULL  , 
	`rename_time` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP , 
	`old_name` varchar(45) COLLATE utf8_general_ci NOT NULL  , 
	`new_name` varchar(45) COLLATE utf8_general_ci NOT NULL  
) ENGINE=MyISAM DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 162) ON DUPLICATE KEY UPDATE version = 162;