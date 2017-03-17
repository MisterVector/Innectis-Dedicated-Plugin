INSERT INTO version (name,version) VALUES ('database', 48) ON DUPLICATE KEY UPDATE version=48;

CREATE  TABLE ip_gen (
  ip_id INT NOT NULL AUTO_INCREMENT ,
  ip_addr VARCHAR(45) NOT NULL ,
  ip_banned INT NOT NULL DEFAULT '0' ,
  PRIMARY KEY (ip_id) ,
  UNIQUE INDEX ip_addr_UNIQUE (ip_addr ASC) )
ENGINE = MyISAM;

CREATE  TABLE ip_conlog (
  ip_id INT NOT NULL ,
  name VARCHAR(45) NOT NULL ,
  con_date TIMESTAMP NOT NULL DEFAULT current_timestamp ,
  PRIMARY KEY (ip_id, name, con_date) ,
  INDEX FK_ip_id (ip_id ASC) ,
  CONSTRAINT FK_ip_id
    FOREIGN KEY (ip_id )
    REFERENCES otakucraft.ip_gen (ip_id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM;

CREATE  TABLE banned_players (
  ban_id INT NOT NULL AUTO_INCREMENT ,
  name VARCHAR(45) NOT NULL ,
  end_date TIMESTAMP NULL ,
  date_lifted TIMESTAMP NULL ,
  PRIMARY KEY (ban_id) )
ENGINE = MyISAM;
