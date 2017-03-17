DELETE FROM `chunk_data` WHERE `key` = 'FRZ_BLK';

INSERT INTO version (name,version) VALUES ('database', 147) ON DUPLICATE KEY UPDATE version = 147;