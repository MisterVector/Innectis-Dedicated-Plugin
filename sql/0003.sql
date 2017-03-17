ALTER TABLE `chests` 
	DROP KEY `coord2`, add KEY `coord2`(`locx2`,`locy2`,`locz2`), COMMENT='';