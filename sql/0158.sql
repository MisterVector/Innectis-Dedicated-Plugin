ALTER TABLE `players` 
	CHANGE `valutas` `valutas` int(11)   NOT NULL DEFAULT '0' after `vote_points`, 
	CHANGE `valutas_in_bank` `valutas_in_bank` int(11)   NOT NULL DEFAULT '0' after `valutas`, 
	CHANGE `valutas_to_bank` `valutas_to_bank` int(11)   NULL DEFAULT '0' after `valutas_in_bank`, 
	CHANGE `valutas_to_player` `valutas_to_player` int(11)   NULL DEFAULT '0' after `valutas_to_bank`, COMMENT='';

ALTER TABLE `the_end_pot` 
	CHANGE `value` `value` int(11)   NULL DEFAULT '0' after `player`, COMMENT='';
INSERT INTO version (name,version) VALUES ('database', 158) ON DUPLICATE KEY UPDATE version = 158;