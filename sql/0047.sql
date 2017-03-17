INSERT INTO version (name,version) VALUES ('database', 47) ON DUPLICATE KEY UPDATE version=47;

CREATE TABLE player_permission (
  name VARCHAR(50) NOT NULL ,
  permissionid INT NOT NULL ,
  PRIMARY KEY (name, permissionid))
ENGINE = MyISAM;



