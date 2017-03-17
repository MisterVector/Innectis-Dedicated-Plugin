/* Drop in Second database */
DROP TABLE `item_shop`;

/* Drop in Second database */
DROP TABLE `shop_members`;

INSERT INTO version (name,version) VALUES ('database', 145) ON DUPLICATE KEY UPDATE version = 145;
