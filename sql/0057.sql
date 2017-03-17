INSERT INTO version (name,version) VALUES ('database', 57) ON DUPLICATE KEY UPDATE version=57;

UPDATE lots SET flags = flags & ~128;
UPDATE lots SET flags = flags & ~256;
