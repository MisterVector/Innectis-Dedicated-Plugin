DROP TABLE `member_groups`;
DROP TABLE `member_users`;

INSERT INTO version (name,version) VALUES ('database', 167) ON DUPLICATE KEY UPDATE version = 167;