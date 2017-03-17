drop table `game_profiles`;

CREATE  TABLE `game_profiles` (
  `username` VARCHAR(60) NOT NULL ,
  `flags` BIGINT NULL ,
  `score` INT NULL ,
  PRIMARY KEY (`username`) );
  
  CREATE  TABLE `game_profiles_scores` (
  `username` VARCHAR(60) NULL ,
  `score_type` VARCHAR(60) NULL ,
  `score` INT NULL ,
  PRIMARY KEY (`username`,`score_type`) );
  
INSERT INTO version (name,version) VALUES ('database', 139) ON DUPLICATE KEY UPDATE version = 139;