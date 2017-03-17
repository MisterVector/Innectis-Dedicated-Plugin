ALTER TABLE `players` 
	ADD COLUMN `pvp_points` int(11)   NOT NULL DEFAULT '0' after `onlinetime`, 
	ADD COLUMN `referral_points` int(11)   NOT NULL DEFAULT '0' after `pvp_points`, 
	ADD COLUMN `vote_points` int(11)   NOT NULL DEFAULT '0' after `referral_points`, 
	ADD COLUMN `valutas` double   NOT NULL DEFAULT '0' after `vote_points`, 
	ADD COLUMN `valutas_in_bank` double   NOT NULL DEFAULT '0' after `valutas`, 
	ADD COLUMN `valutas_to_bank` double   NULL DEFAULT '0' after `valutas_in_bank`, 
	ADD COLUMN `valutas_to_player` double   NULL DEFAULT '0' after `valutas_to_bank`, 
	CHANGE `settings` `settings` bigint(20)   NOT NULL DEFAULT '0' after `valutas_to_player`, 
	CHANGE `timezone` `timezone` varchar(30)  COLLATE latin1_swedish_ci NULL after `settings`, 
	CHANGE `playergroup` `playergroup` int(11)   NOT NULL DEFAULT '-1' after `timezone`, 
	CHANGE `namecolour` `namecolour` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' COMMENT 'Hexvalue' after `playergroup`, 
	CHANGE `backpack` `backpack` bigint(20)   NULL after `namecolour`, 
	CHANGE `ignorelist` `ignorelist` text  COLLATE latin1_swedish_ci NULL after `backpack`, 
	DROP COLUMN `pvppoints`, 
	DROP COLUMN `referralpoints`, 
	DROP COLUMN `votepoints`, 
	DROP COLUMN `balance`, 
	DROP COLUMN `bank`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 155) ON DUPLICATE KEY UPDATE version = 155;