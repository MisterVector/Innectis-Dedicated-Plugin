INSERT INTO version (name,version) VALUES ('database', 81) ON DUPLICATE KEY UPDATE version=81;

ALTER TABLE `banned_players` 
	ADD COLUMN `end_time` timestamp   NULL after `is_ipbanned`, 
	DROP COLUMN `end_date`, COMMENT='';