
INSERT INTO version (name,version) VALUES ('database', 42) ON DUPLICATE KEY UPDATE 
version=42;


ALTER TABLE vote_log 
	ADD KEY time(time), COMMENT='';

ALTER TABLE vote_services 
	CHANGE ip ip varchar(30)  COLLATE latin1_swedish_ci NOT NULL first, 
	ADD COLUMN domain varchar(60)  COLLATE latin1_swedish_ci NOT NULL after ip, 
	DROP COLUMN note, 
	ADD KEY domain(domain), COMMENT='';