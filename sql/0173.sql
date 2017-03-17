ALTER TABLE `warps` 
	ADD COLUMN `settings` bigint(20)   NOT NULL after `locz`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL DEFAULT '0' after `settings`, 
	DROP COLUMN `hidden`, COMMENT='';
INSERT INTO version (name,version) VALUES ('database', 173) ON DUPLICATE KEY UPDATE version = 173;