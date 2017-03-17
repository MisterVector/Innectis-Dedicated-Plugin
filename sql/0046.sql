INSERT INTO version (name,version) VALUES ('database', 46) ON DUPLICATE KEY UPDATE version=46;

ALTER TABLE stored_inventory ADD COLUMN converted INT NOT NULL DEFAULT '0'  AFTER inventory ;