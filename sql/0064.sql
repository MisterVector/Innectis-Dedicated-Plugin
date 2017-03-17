INSERT INTO version (name,version) VALUES ('database', 64) ON DUPLICATE KEY UPDATE version=64;

DROP TABLE `pvp_kills`; 