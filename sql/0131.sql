#version 130

CREATE  TABLE switches (
  switchid INT NOT NULL AUTO_INCREMENT,
  owner VARCHAR(50) NOT NULL ,
  locx INT NOT NULL ,
  locy INT NOT NULL ,
  locz INT NOT NULL ,
  world VARCHAR(50) NOT NULL ,
  flags BIGINT NOT NULL ,
  PRIMARY KEY (switchid) );


CREATE  TABLE switches_links (
  switchA INT NOT NULL ,
  switchB INT NOT NULL ,
  PRIMARY KEY (switchA, switchB) );

#Add indexes
ALTER TABLE switches ADD INDEX location (locx ASC, locy ASC, locz ASC, world ASC) ;
ALTER TABLE switches_links ADD INDEX switchA (switchA ASC), ADD INDEX switchB (switchB ASC) ;

# update version
INSERT INTO version (name,version) VALUES ('database', 131) ON DUPLICATE KEY UPDATE version = 131;