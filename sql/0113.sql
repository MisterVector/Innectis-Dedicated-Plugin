INSERT INTO version (name,version) VALUES ('database', 113) ON DUPLICATE KEY UPDATE version=113;

ALTER TABLE ip_log DROP INDEX ip_name_UNIQUE;

ALTER TABLE ip_log CHANGE COLUMN logid logid BIGINT NOT NULL AUTO_INCREMENT;


CREATE  TABLE player_password (
  username VARCHAR(60) NOT NULL ,
  password BLOB NOT NULL ,
  dateset TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (username) );

  
CREATE  TABLE player_failedlogin (
  logid BIGINT NOT NULL AUTO_INCREMENT ,
  username VARCHAR(60) NOT NULL ,
  ip VARCHAR(60) NOT NULL ,
  logdate TIMESTAMP NOT NULL ,
  PRIMARY KEY (logid) );

