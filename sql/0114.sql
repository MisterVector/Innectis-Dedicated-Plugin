#version 114

# update invalid entries
UPDATE players SET backpack = NULL WHERE length(backpack) = 0;
# alter table
ALTER TABLE players CHANGE COLUMN backpack backpack BIGINT NULL DEFAULT NULL;

# update version
INSERT INTO version (name,version) VALUES ('database', 114) ON DUPLICATE KEY UPDATE version=114;