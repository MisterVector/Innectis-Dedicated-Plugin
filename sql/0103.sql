INSERT INTO version (name,version) VALUES ('database', 102) ON DUPLICATE KEY UPDATE version=102;

ALTER TABLE `otakucraft`.`waypoints` CHANGE COLUMN `tyaw` `tyaw` FLOAT(11) NOT NULL  ;