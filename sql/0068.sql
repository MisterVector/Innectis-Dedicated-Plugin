INSERT INTO version (name,version) VALUES ('database', 68) ON DUPLICATE KEY UPDATE version=68;

UPDATE lots SET y1=-1, y2=257;
