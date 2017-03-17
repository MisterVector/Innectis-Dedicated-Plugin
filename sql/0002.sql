ALTER TABLE `chatlog` 
	ADD KEY `date`(`date`), 
	ADD KEY `username`(`username`), COMMENT='';


ALTER TABLE `chestlog` 
	ADD KEY `coords`(`chestx`,`chesty`,`chestz`), 
	ADD KEY `date`(`date`), 
	ADD KEY `name`(`name`), COMMENT='';


ALTER TABLE `chests` 
	ADD UNIQUE KEY `coord1`(`locx1`,`locy1`,`locz1`), 
	ADD UNIQUE KEY `coord2`(`locx2`,`locy2`,`locz2`), 
	DROP KEY `locX1`, 
	DROP KEY `locX2`, 
	DROP KEY `locY1`, 
	DROP KEY `locY2`, 
	DROP KEY `locZ1`, 
	DROP KEY `locZ2`, COMMENT='';


ALTER TABLE `doors` 
	ADD UNIQUE KEY `coords`(`locx`,`locy`,`locz`), 
	DROP KEY `locX`, 
	DROP KEY `locY`, 
	DROP KEY `locZ`, COMMENT='';


ALTER TABLE `homes` 
	ADD UNIQUE KEY `coords`(`locx`,`locy`,`locz`), COMMENT='';


ALTER TABLE `lots` 
	ADD UNIQUE KEY `coords`(`x1`,`x2`,`z1`,`z2`), COMMENT='';


ALTER TABLE `npcs` 
	ADD UNIQUE KEY `coords`(`locx`,`locy`,`locz`), COMMENT='';