INSERT INTO version (name,version) VALUES ('database', 111) ON DUPLICATE KEY UPDATE version=111;

ALTER TABLE players
    ADD COLUMN settings BIGINT NOT NULL DEFAULT 0  AFTER bank;

ALTER TABLE players 
    DROP COLUMN showdeathmsg, 
    DROP COLUMN shopnotification,
    DROP COLUMN starvation ;

