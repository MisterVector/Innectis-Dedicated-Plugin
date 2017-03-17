INSERT INTO version (name,version) VALUES ('database', 100) ON DUPLICATE KEY UPDATE version=100;

/** FIRST ADD THE NEW FIELD TO THE `banned_players` TABLE **/
ALTER TABLE `banned_players` 
	ADD COLUMN `duration_ticks` bigint(20) NOT NULL DEFAULT '0' after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1) NULL DEFAULT '0' after `duration_ticks`, COMMENT='';

/** FILL THE DURATION TICKS WITH THE NEW VALUE **/
UPDATE `banned_players` SET `duration_ticks`= (UNIX_TIMESTAMP(`end_time`) - UNIX_TIMESTAMP(`banned_time`)) * 1000 WHERE `end_time` IS NOT NULL;

ALTER TABLE `banned_players` DROP COLUMN `end_time`, COMMENT='';
