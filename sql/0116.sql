#version 116

# remove unsupported chests (there are none on live server)
DELETE FROM enderchests WHERE length(contents) > 6;


# alter table
ALTER TABLE enderchests CHANGE COLUMN contents bagid BIGINT NULL DEFAULT NULL;

# update version
INSERT INTO version (name,version) VALUES ('database', 116) ON DUPLICATE KEY UPDATE version = 116;