ALTER TABLE `owned_object_tags` 
	ADD COLUMN `public_tag` tinyint(4)   NOT NULL after `tag`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 168) ON DUPLICATE KEY UPDATE version = 168;