INSERT INTO version (name,version) VALUES ('database', 74) ON DUPLICATE KEY UPDATE version=74;

CREATE TABLE lots_backup LIKE lots;
INSERT INTO lots_backup SELECT * FROM lots;

UPDATE lots
	SET x1=LEAST(x1,x2)+1, x2=GREATEST(x1,x2)-1,
	y1=LEAST(y1,y2)+1, y2=GREATEST(y1,y2)-1,
	z1=LEAST(z1,z2)+1, z2=GREATEST(z1,z2)-1;