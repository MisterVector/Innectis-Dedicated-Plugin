INSERT INTO version (name,version) VALUES ('database', 62) ON DUPLICATE KEY UPDATE version=62;

ALTER TABLE `waypoints` 
	ADD COLUMN `flags` int(11)   NOT NULL after `tyaw`;