INSERT INTO version (name,version) VALUES ('database', 103) 
ON DUPLICATE KEY UPDATE version=103;


CREATE  TABLE `otakucraft`.`configvalues` (
  `ckey` VARCHAR(50) NOT NULL ,
  `cvalue` TEXT NOT NULL ,
  PRIMARY KEY (`ckey`) );

