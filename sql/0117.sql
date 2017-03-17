#version 117

# insert poll tables
CREATE  TABLE poll_list (
  ID INT NOT NULL AUTO_INCREMENT ,
  question VARCHAR(100) NULL ,
  active TINYINT(1) NULL DEFAULT '0' ,
  multipleChoice TINYINT(1) NULL DEFAULT '0' ,
  minUserGroup INT NULL DEFAULT '8' ,
  reward BIGINT NULL DEFAULT '0' ,
  PRIMARY KEY (ID) );
  
  
 CREATE  TABLE poll_options (
  ID INT NULL ,
  optionchoice VARCHAR(45) NULL ,
  answer VARCHAR(45) NULL ,
  correct TINYINT(1) NULL DEFAULT '0' );
  
 CREATE  TABLE poll_answers (
  ID INT NULL ,
  username VARCHAR(45) NULL ,
  answer VARCHAR(45) NULL );

  
  ALTER TABLE poll_options ADD COLUMN key INT NOT NULL AUTO_INCREMENT  FIRST , ADD PRIMARY KEY (key) ;

# update version
INSERT INTO version (name,version) VALUES ('database', 117) ON DUPLICATE KEY UPDATE version = 117;