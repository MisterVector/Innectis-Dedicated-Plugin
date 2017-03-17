INSERT INTO version (name,version) VALUES ('database', 86) ON DUPLICATE KEY UPDATE version=86;

ALTER TABLE `vote_log` 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL first, 
	CHANGE `ip` `ip` varchar(60)  COLLATE latin1_swedish_ci NULL after `username`, 
	CHANGE `service` `service` varchar(60)  COLLATE latin1_swedish_ci NULL after `ip`, 
	ADD COLUMN `timestamp` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `service`, 
	DROP COLUMN `time`, 
	DROP COLUMN `service_address`, 
	DROP KEY `ip`, 
	DROP KEY `time`, COMMENT='';

DROP TABLE `vote_services`; 