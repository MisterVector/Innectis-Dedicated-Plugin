UPDATE lots SET flags = flags ^ 4398046511104 WHERE flags & 4398046511104 = 4398046511104;

INSERT INTO version (name,version) VALUES ('database', 148) ON DUPLICATE KEY UPDATE version = 148;