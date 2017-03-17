INSERT INTO version (name,version) VALUES ('database', 43) ON DUPLICATE KEY UPDATE version=43;

ALTER TABLE lots 
	DROP KEY lotnumbers, add KEY lotnumbers(owner,lotnr);