-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              10.5.9-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win64
-- HeidiSQL Versione:            11.2.0.6213
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dump della struttura del database tesi
CREATE DATABASE IF NOT EXISTS `tesi` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `tesi`;

-- Dump della struttura di tabella tesi.ordini
CREATE TABLE IF NOT EXISTS `ordini` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Sorgente` varchar(50) DEFAULT NULL,
  `Destinazione` varchar(50) DEFAULT NULL,
  `Peso` float DEFAULT NULL,
  `Volume` float DEFAULT NULL,
  `Data` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella tesi.ordini_consegnati
CREATE TABLE IF NOT EXISTS `ordini_consegnati` (
  `ID` int(11) DEFAULT NULL,
  `citta_consegna` varchar(90) DEFAULT NULL,
  `data` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella tesi.tratte
CREATE TABLE IF NOT EXISTS `tratte` (
  `Partenza` varchar(18) NOT NULL,
  `Destinazione` varchar(18) NOT NULL,
  `Distanza_km` varchar(50) NOT NULL DEFAULT '0',
  `Mezzo_di_trasporto` varchar(14) NOT NULL,
  `Emissioni_g` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- L’esportazione dei dati non era selezionata.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
