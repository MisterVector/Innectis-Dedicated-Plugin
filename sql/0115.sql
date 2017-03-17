#version 115

# alter table
ALTER TABLE player_inventory CHANGE COLUMN content bagid BIGINT NULL DEFAULT NULL;

# update version
INSERT INTO version (name,version) VALUES ('database', 115) ON DUPLICATE KEY UPDATE version = 115;