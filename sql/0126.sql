UPDATE chests SET flags = flags ^ 1 WHERE flags & 1 = 1;
UPDATE chests SET flags = flags ^ 8 WHERE flags & 8 = 8;
UPDATE lots SET flags = flags ^ 536870912 WHERE flags & 536870912 = 536870912;

INSERT INTO version (name,version) VALUES ('database', 126) ON DUPLICATE KEY UPDATE version = 126;