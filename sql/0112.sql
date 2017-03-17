INSERT INTO version (name,version) VALUES ('database', 112) ON DUPLICATE KEY UPDATE version=112;

/** Make sure bit 7 is off, as the setting is not used anymore **/
UPDATE players SET settings = settings ^ 64 WHERE settings & 64 = 64;