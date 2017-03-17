UPDATE lots SET y1 = 0 WHERE y1 = 1;
UPDATE lots SET y2 = 255 WHERE y2 > 255;

INSERT INTO version (name,version) VALUES ('database', 150) ON DUPLICATE KEY UPDATE version = 150;