INSERT INTO version (name,version) VALUES ('database', 76) ON DUPLICATE KEY UPDATE version=76;

/* Drop in Second database */
DROP TABLE `banned_ip_logger`; 
