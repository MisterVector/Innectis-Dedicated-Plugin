#version 118

ALTER TABLE banned_ip_players ADD COLUMN expired BIGINT NOT NULL DEFAULT 0  AFTER joinban ;
ALTER TABLE banned_players ADD COLUMN expired BIGINT NOT NULL DEFAULT 0  AFTER joinban ;


# update version
INSERT INTO version (name,version) VALUES ('database', 130) ON DUPLICATE KEY UPDATE version = 130;