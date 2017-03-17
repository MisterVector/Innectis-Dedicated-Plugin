INSERT INTO `version` (`name`,`version`) VALUES ('database', 7) ON DUPLICATE KEY UPDATE `version`=7;

ALTER TABLE `otakucraft`.`chests_members` 
DROP PRIMARY KEY 
, ADD PRIMARY KEY (`chestid`, `username`) ;
