#version 119

CREATE TABLE presents (
  presentid int(11) NOT NULL AUTO_INCREMENT,
  creator varchar(60) NOT NULL,
  title varchar(100) NOT NULL,
  bagid bigint(20) NOT NULL,
  opened bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (presentid)
) ENGINE=MyISAM;


# update version
INSERT INTO version (name,version) VALUES ('database', 119) ON DUPLICATE KEY UPDATE version = 119;