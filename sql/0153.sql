UPDATE players SET playergroup = playergroup + 1 WHERE playergroup >= 5;

INSERT INTO version (name,version) VALUES ('database', 153) ON DUPLICATE KEY UPDATE version = 153;