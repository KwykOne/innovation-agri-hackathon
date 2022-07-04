-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.21 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for farm
CREATE DATABASE IF NOT EXISTS `farm` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `farm`;

-- Dumping structure for table farm.catalogue
CREATE TABLE IF NOT EXISTS `catalogue` (
  `oid_index` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `fk_item_type` smallint(4) DEFAULT '1',
  `price` char(50) DEFAULT NULL,
  `units` char(50) DEFAULT NULL,
  `quantity` char(50) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `entry_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid_index`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table farm.catalogue: 1 rows
/*!40000 ALTER TABLE `catalogue` DISABLE KEYS */;
INSERT INTO `catalogue` (`oid_index`, `name`, `fk_item_type`, `price`, `units`, `quantity`, `description`, `img_url`, `entry_time`) VALUES
	(1, '', 1, '', '', '10', '', NULL, '2022-07-04 21:23:06');
/*!40000 ALTER TABLE `catalogue` ENABLE KEYS */;

-- Dumping structure for table farm.farm
CREATE TABLE IF NOT EXISTS `farm` (
  `oid_index` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `area` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `lat` float NOT NULL,
  `lng` float NOT NULL,
  `fk_type_set` set('1','2','3','4','5','6','7','8') NOT NULL DEFAULT '1',
  `payment_options` set('WALKIN','COD','PHONEPE','PAYTM','GOOGLEPAY') NOT NULL DEFAULT 'COD',
  `fk_owner` int(11) DEFAULT NULL,
  PRIMARY KEY (`oid_index`),
  UNIQUE KEY `name_area` (`name`,`area`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Dumping data for table farm.farm: 1 rows
/*!40000 ALTER TABLE `farm` DISABLE KEYS */;
INSERT INTO `farm` (`oid_index`, `name`, `area`, `city`, `lat`, `lng`, `fk_type_set`, `payment_options`, `fk_owner`) VALUES
	(1, 'HUMUS', 'Vidyaranyapura', 'Bangalore', 55, 45, '1', 'COD', 1);
/*!40000 ALTER TABLE `farm` ENABLE KEYS */;

-- Dumping structure for table farm.item_types
CREATE TABLE IF NOT EXISTS `item_types` (
  `oid_index` smallint(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '0',
  `description` text,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid_index`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

-- Dumping data for table farm.item_types: 8 rows
/*!40000 ALTER TABLE `item_types` DISABLE KEYS */;
INSERT INTO `item_types` (`oid_index`, `name`, `description`, `created_on`) VALUES
	(1, 'VEGETABLES', NULL, '2022-07-04 21:01:42'),
	(2, 'CATTLE', NULL, '2022-07-04 21:01:57'),
	(3, 'POULTERY', NULL, '2022-07-04 21:02:06'),
	(4, 'FISHERY', NULL, '2022-07-04 21:02:13'),
	(5, 'SPICES', NULL, '2022-07-04 21:02:29'),
	(6, 'FRUITS', NULL, '2022-07-04 21:02:39'),
	(7, 'RAWMATERIAL', NULL, '2022-07-04 21:03:42'),
	(8, 'DRYFRUITS', NULL, '2022-07-04 21:04:39');
/*!40000 ALTER TABLE `item_types` ENABLE KEYS */;

-- Dumping structure for table farm.owner
CREATE TABLE IF NOT EXISTS `owner` (
  `oid_index` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `mobile_number` varchar(15) DEFAULT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid_index`),
  UNIQUE KEY `mobile_number` (`mobile_number`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table farm.owner: 1 rows
/*!40000 ALTER TABLE `owner` DISABLE KEYS */;
INSERT INTO `owner` (`oid_index`, `name`, `mobile_number`, `creation_date`) VALUES
	(1, 'Narayanan', '919999999999', '2022-07-04 20:56:28');
/*!40000 ALTER TABLE `owner` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
