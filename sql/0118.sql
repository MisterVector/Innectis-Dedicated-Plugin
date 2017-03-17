#version 118

# insert poll tables

drop table if exists  poll_list;
CREATE  TABLE poll_list (
  ID INT NOT NULL AUTO_INCREMENT ,
  question VARCHAR(100) NULL ,
  active TINYINT(1) NULL DEFAULT '0' ,
  multipleChoice TINYINT(1) NULL DEFAULT '0' ,
  minUserGroup INT NULL DEFAULT '8' ,
  reward BIGINT NULL DEFAULT '0' ,
  PRIMARY KEY (ID) );
  
drop table if exists  poll_options;
 CREATE  TABLE poll_options (
  ID INT NOT NULL,
  optionchoice VARCHAR(45) NULL ,
  answer VARCHAR(20) NULL ,
  correct TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (ID, optionchoice));
  
  
  
drop table if exists  poll_answers;
CREATE  TABLE poll_answers (
  ID INT NULL ,
  username VARCHAR(60) NULL,
  answer VARCHAR(100) NULL,
  PRIMARY KEY (id, username) );

# update version
INSERT INTO version (name,version) VALUES ('database', 118) ON DUPLICATE KEY UPDATE version = 118;