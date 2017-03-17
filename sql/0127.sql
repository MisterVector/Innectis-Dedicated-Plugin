ALTER TABLE `chests` ADD COLUMN `typeid` int(11)   NOT NULL DEFAULT '0' after `chestid`;

INSERT INTO version (name,version) VALUES ('database', 127) ON DUPLICATE KEY UPDATE version = 127;