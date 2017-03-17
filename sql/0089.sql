INSERT INTO version (name,version) VALUES ('database', 89) ON DUPLICATE KEY UPDATE version=89;

DROP TABLE `shop_signpool`; 