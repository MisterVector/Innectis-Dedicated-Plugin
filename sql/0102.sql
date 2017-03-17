INSERT INTO version (name,version) VALUES ('database', 102) ON DUPLICATE KEY UPDATE version=102;

ALTER TABLE `player_permission` 
	ADD COLUMN `disabled` tinyint(1)   NOT NULL DEFAULT '0' after `permissionid`, COMMENT='';