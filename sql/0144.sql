
INSERT INTO version (name,version) VALUES ('database', 144) ON DUPLICATE KEY UPDATE version = 144;

ALTER TABLE players ADD COLUMN `timezone` VARCHAR(30) NULL  AFTER `settings` ;

