
/**
 * 
 * This file also includes the import function to import the CSV files.	
 * Don't forget those. Keep in mind they do take up some space.
 * In total about 4 million rows of data..
 * This also includes a version number for the geo data.
 */

INSERT INTO version (name,version) VALUES ('database', 65) ON DUPLICATE KEY UPDATE version=65;
INSERT INTO version (name,version) VALUES ('geodatadatabase', 1) ON DUPLICATE KEY UPDATE version=1;

CREATE  TABLE `geolite_blocks` (
  `startIpNum` BIGINT NOT NULL ,
  `endIpNum` BIGINT NOT NULL ,
  `locId` INT NOT NULL )
ENGINE = MyISAM;

/**
mysqlimport --fields-terminated-by="," --fields-optionally-enclosed-by="\"" --lines-terminated-by="\n" --host=localhost --user=root --password=root otakucraft D:\RMH\Mijn Documenten\Dropbox\Shared Otakucraft\GeoData\geolite_blocks.csv*/

CREATE  TABLE `geolite_location` (
  `locId` INT NOT NULL ,
  `country` CHAR(2) NOT NULL ,
  `region` CHAR(2) NOT NULL ,
  `city` VARCHAR(45) NOT NULL ,
  `postalCode` VARCHAR(10) NOT NULL ,
  `latitude` LONG NOT NULL ,
  `longitude` LONG NOT NULL ,
  PRIMARY KEY (`locId`) )
ENGINE = MyISAM;

/*
mysqlimport --fields-terminated-by="," --fields-optionally-enclosed-by="\"" --lines-terminated-by="\n" --host=localhost --user=root --password=root otakucraft 
D:\Dropbox\Shared Otakucraft\GeoData\Geo\geolite_location.csv
*/