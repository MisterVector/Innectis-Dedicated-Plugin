/* Initial insertion */
ALTER TABLE `waypoints` 
	ADD COLUMN `cost_type` int(11)   NOT NULL after `forced`, COMMENT='';

/* Run these statements to convert the existing boolean into cost_type */
UPDATE waypoints SET cost_type = 1 WHERE forced = true;
UPDATE waypoints SET cost_type = 2 WHERE forced = false;

/* Run this after above statements */
ALTER TABLE `waypoints` 
	DROP COLUMN `forced`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 181) ON DUPLICATE KEY UPDATE version = 181;