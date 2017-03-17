DROP TABLE `poll_answers`; 
DROP TABLE `poll_list`; 
DROP TABLE `poll_options`; 

INSERT INTO version (name,version) VALUES ('database', 179) ON DUPLICATE KEY UPDATE version = 179;