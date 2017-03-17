INSERT INTO version (name,version) VALUES ('database', 106) ON DUPLICATE KEY UPDATE version=106;

ALTER TABLE `bookcase` 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `locz`, COMMENT='';

ALTER TABLE `chests` 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `locz2`, COMMENT='';

ALTER TABLE `lots` 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `yaw`, COMMENT='';

ALTER TABLE `waypoints` 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `tyaw`, COMMENT='';