INSERT INTO version (name,version) VALUES ('database', 49) ON DUPLICATE KEY UPDATE version=49;


CREATE TABLE lot_names(
	lotname varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	lotid int(11) NOT NULL  , 
	time bigint(20) NOT NULL  , 
	UNIQUE KEY lotid(lotid) , 
	KEY lotname(lotname) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


ALTER TABLE lots 
	ADD COLUMN lotname varchar(60)  COLLATE latin1_swedish_ci NOT NULL after world, 
	CHANGE x1 x1 int(11)   NOT NULL after lotname, 
	ADD COLUMN y1 int(11)   NOT NULL DEFAULT '-1' after x1, 
	CHANGE z1 z1 int(11)   NOT NULL after y1, 
	CHANGE x2 x2 int(11)   NOT NULL after z1, 
	ADD COLUMN y2 int(11)   NOT NULL DEFAULT '-1' after x2, 
	CHANGE z2 z2 int(11)   NOT NULL after y2, 
	CHANGE sx sx int(11)   NOT NULL after z2, 
	CHANGE sy sy int(11)   NOT NULL after sx, 
	CHANGE sz sz int(11)   NOT NULL after sy, 
	CHANGE yaw yaw int(11)   NOT NULL DEFAULT '0' after sz, 
	CHANGE flags flags int(11)   NOT NULL DEFAULT '0' after yaw, 
	CHANGE creator creator varchar(60)  COLLATE latin1_swedish_ci NOT NULL after flags, 
	CHANGE lastedit lastedit bigint(20)   NOT NULL after creator, 
	CHANGE hidden hidden tinyint(1)   NOT NULL after lastedit, 
	ADD KEY lotname(lotname);

ALTER TABLE prefix 
	ADD UNIQUE KEY prefix_UNIQUE(name,subid), 
	DROP KEY prefixid_UNIQUE, ENGINE=MyISAM; 

ALTER TABLE referral_forum_cache ENGINE=MyISAM; 

ALTER TABLE vote_log ENGINE=MyISAM; 

ALTER TABLE vote_services ENGINE=MyISAM; 