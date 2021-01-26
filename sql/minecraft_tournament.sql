-- phpMyAdmin SQL Dump
-- version 4.5.4.1
-- http://www.phpmyadmin.net
--
-- Värd: localhost
-- Tid vid skapande: 26 jan 2021 kl 23:00
-- Serverversion: 5.7.11
-- PHP-version: 5.6.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databas: `minecraft_tournament`
--
CREATE DATABASE IF NOT EXISTS `minecraft_tournament` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `minecraft_tournament`;

DELIMITER $$
--
-- Procedurer
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `reset_data` ()  BEGIN
	UPDATE teams SET score = 0;
    
    UPDATE players SET score = 0, team_number = 0, kills = 0;
    
    UPDATE tc_data SET data_value = null WHERE data_key = "active_server";
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellstruktur `global_chat_log`
--

CREATE TABLE `global_chat_log` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `server_name` text COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin NOT NULL,
  `is_command` tinyint(1) NOT NULL,
  `canceled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Tabellstruktur `players`
--

CREATE TABLE `players` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) COLLATE utf8_bin NOT NULL,
  `username` varchar(16) COLLATE utf8_bin NOT NULL,
  `team_number` int(11) NOT NULL DEFAULT '-1',
  `score` int(11) NOT NULL DEFAULT '0',
  `kills` int(11) NOT NULL DEFAULT '0',
  `has_joined` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Tabellstruktur `tc_data`
--

CREATE TABLE `tc_data` (
  `id` int(10) UNSIGNED NOT NULL,
  `data_key` text COLLATE utf8_bin NOT NULL,
  `data_value` text COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumpning av Data i tabell `tc_data`
--

INSERT INTO `tc_data` (`id`, `data_key`, `data_value`) VALUES
(1, 'active_server', 'survivalgames'),
(4, 'tournament_name', 'TournamentCore');

-- --------------------------------------------------------

--
-- Tabellstruktur `teams`
--

CREATE TABLE `teams` (
  `id` int(11) NOT NULL,
  `team_number` int(11) NOT NULL,
  `score` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Index för dumpade tabeller
--

--
-- Index för tabell `global_chat_log`
--
ALTER TABLE `global_chat_log`
  ADD PRIMARY KEY (`id`);

--
-- Index för tabell `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`);

--
-- Index för tabell `tc_data`
--
ALTER TABLE `tc_data`
  ADD PRIMARY KEY (`id`);

--
-- Index för tabell `teams`
--
ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `team_number` (`team_number`);

--
-- AUTO_INCREMENT för dumpade tabeller
--

--
-- AUTO_INCREMENT för tabell `global_chat_log`
--
ALTER TABLE `global_chat_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;
--
-- AUTO_INCREMENT för tabell `players`
--
ALTER TABLE `players`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT för tabell `tc_data`
--
ALTER TABLE `tc_data`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT för tabell `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
