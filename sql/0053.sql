INSERT INTO version (name,version) VALUES ('database', 53) ON DUPLICATE KEY UPDATE version=53;


ALTER TABLE shop_items_official 
	ADD COLUMN cost INT(11)   NOT NULL AFTER Data, COMMENT='';