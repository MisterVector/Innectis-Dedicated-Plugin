ALTER TABLE `lots` 
	CHANGE `enter_msg` `enter_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `creator`, 
	CHANGE `exit_msg` `exit_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `enter_msg`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 138) ON DUPLICATE KEY UPDATE version = 138;