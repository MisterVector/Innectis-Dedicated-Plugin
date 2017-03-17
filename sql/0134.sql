CREATE  TABLE `innectis_db`.`game_profiles` (
  `username` VARCHAR(45) NULL ,
  `score` INT NULL ,
  `flags` BIGINT NULL );

INSERT INTO version (name,version) VALUES ('database', 134) ON DUPLICATE KEY UPDATE version = 134;