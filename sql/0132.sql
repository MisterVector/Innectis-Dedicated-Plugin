ALTER TABLE `lots` 
	ADD COLUMN `enter_msg` varchar(60)  COLLATE latin1_swedish_ci NULL after `creator`, 
	ADD COLUMN `exit_msg` varchar(60)  COLLATE latin1_swedish_ci NULL after `enter_msg`, 
	CHANGE `lastedit` `lastedit` bigint(20)   NOT NULL after `exit_msg`, 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `lastedit`, 
	CHANGE `deleted` `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, COMMENT='';

UPDATE lots SET enter_msg = (SELECT message FROM lot_messages WHERE lots.lotid = lot_messages.lotid AND lot_messages.type = 0);
UPDATE lots SET exit_msg = (SELECT message FROM lot_messages WHERE lots.lotid = lot_messages.lotid AND lot_messages.type = 1);

DROP TABLE `lot_messages`; 