ALTER TABLE `players` 
	CHANGE `vote_points` `vote_points` double   NOT NULL DEFAULT '0' after `pvp_points`, 
	CHANGE `valutas` `valutas` double   NOT NULL DEFAULT '0' after `vote_points`, 
	CHANGE `valutas_in_bank` `valutas_in_bank` double   NOT NULL DEFAULT '0' after `valutas`, 
	CHANGE `valutas_to_bank` `valutas_to_bank` double   NOT NULL DEFAULT '0' after `valutas_in_bank`, 
	CHANGE `valutas_to_player` `valutas_to_player` double   NOT NULL DEFAULT '0' after `valutas_to_bank`, 
	CHANGE `settings` `settings` double   NOT NULL DEFAULT '0' after `valutas_to_player`, 
	CHANGE `timezone` `timezone` varchar(90)  COLLATE latin1_swedish_ci NULL after `settings`, 
	CHANGE `playergroup` `playergroup` double   NOT NULL DEFAULT '-1' after `timezone`, 
	CHANGE `namecolour` `namecolour` varchar(30)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' after `playergroup`, 
	CHANGE `backpack` `backpack` double   NULL after `namecolour`, 
	DROP COLUMN `referral_points`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 166) ON DUPLICATE KEY UPDATE version = 166;