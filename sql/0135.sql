ALTER TABLE `lots` 
	ADD COLUMN `warp_count` int(11)   NOT NULL DEFAULT '0' after `last_member_edit`, 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `warp_count`, 
	CHANGE `deleted` `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 135) ON DUPLICATE KEY UPDATE version = 135;