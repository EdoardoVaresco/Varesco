-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mar 14, 2025 alle 09:28
-- Versione del server: 10.4.28-MariaDB
-- Versione PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `biblioteca_servlet`

-- --------------------------------------------------------

--
-- Struttura della tabella `libri`
--

DROP DATABASE IF EXISTS biblioteca_servlet;

CREATE DATABASE biblioteca_servlet DEFAULT CHARACTER SET = utf8;

USE biblioteca_servlet;

CREATE TABLE `libri` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `titolo` VARCHAR(100) NOT NULL,
  `autore` VARCHAR(100) NOT NULL,
  `anno_pubblicazione` DATE NOT NULL,
  `genere` varchar(50) NOT NULL,
  `copie_disponibili` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `libri`
--

INSERT INTO `libri` (`id`, `titolo`, `autore`, `anno_pubblicazione`, `genere`, `copie_disponibili`) VALUES
(1, 'Il Signore degli Anelli', 'J.R.R. Tolkien', 1954, 'Fantasy', 5),
(2, '1984', 'George Orwell', 1949, 'Distopia', 3),
(3, 'Il Nome della Rosa', 'Umberto Eco', 1980, 'Storico', 4),
(4, 'Harry Potter e la Pietra Filosofale', 'J.K. Rowling', 1997, 'Fantasy', 6),
(5, 'Cronache del Ghiaccio e del Fuoco', 'George R.R. Martin', 1996, 'Fantasy', 2),
(6, 'I Promessi Sposi', 'Alessandro Manzoni', 1827, 'Storico', 7),
(7, 'Fahrenheit 451', 'Ray Bradbury', 1953, 'Distopia', 4),
(8, 'Il Grande Gatsby', 'F. Scott Fitzgerald', 1925, 'Classico', 5);

-- --------------------------------------------------------

--
-- Struttura della tabella `utenti`
--

CREATE TABLE `utenti` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `nome` varchar(30) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `ruolo` enum('utente','admin') DEFAULT 'utente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `utenti`
--

INSERT INTO `utenti` (`id`, `nome`, `email`, `password`, `ruolo`) VALUES
(1, 'Marco Rossi', 'marco.rossi@email.com', '$2a$10$xyz123...', 'utente'),
(2, 'Giulia Bianchi', 'giulia.bianchi@email.com', '$2a$10$abc456...', 'utente'),
(3, 'Luca Verdi', 'luca.verdi@email.com', '$2a$10$abc789...', 'utente'),
(4, 'Sara Neri', 'sara.neri@email.com', '$2a$10$def012...', 'utente'),
(5, 'Admin Biblioteca', 'admin@email.com', '$2a$10$pqr789...', 'admin'),
(6, 'Marta Fabbri', 'marta.fabbri@email.com', '$2a$10$ghi345...', 'utente'),
(7, 'Andrea Lupi', 'andrea.lupi@email.com', '$2a$10$xyz567...', 'utente');

--
-- Struttura della tabella `prenotazioni`
--

CREATE TABLE `prenotazioni` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `utente_id` int NOT NULL,
  `libro_id` int NOT NULL,
  `data_prenotazione` date NOT NULL DEFAULT curdate(),
  FOREIGN KEY(utente_id) REFERENCES utenti(id) ON UPDATE CASCADE,
  FOREIGN KEY(libro_id) REFERENCES libri(id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `prenotazioni`
--

INSERT INTO `prenotazioni` (`id`, `utente_id`, `libro_id`, `data_prenotazione`) VALUES
(1, 1, 2, '2025-03-14'),
(2, 2, 1, '2025-03-14'),
(3, 3, 3, '2025-03-14'),
(4, 4, 4, '2025-03-14'),
(5, 5, 6, '2025-03-14'),
(6, 6, 7, '2025-03-14'),
(7, 1, 8, '2025-03-14');

-- --------------------------------------------------------

DELIMITER $$



-- Trigger per controllare l'anno di pubblicazione
CREATE TRIGGER before_insert_libri_anno
BEFORE INSERT ON libri
FOR EACH ROW
BEGIN
    IF NEW.anno_pubblicazione > CURDATE() THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Errore: l'anno di pubblicazione del libro non può essere nel futuro";
    END IF;
END$$

CREATE TRIGGER before_update_libri_anno
BEFORE UPDATE ON libri
FOR EACH ROW
BEGIN
    IF NEW.anno_pubblicazione > CURDATE() THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Errore: l'anno di pubblicazione del libro non può essere nel futuro";
    END IF;
END$$

-- Trigger per controllare le copie disponibili
CREATE TRIGGER before_insert_libri_copie
BEFORE INSERT ON libri
FOR EACH ROW
BEGIN
    IF NEW.copie_disponibili < 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Errore: le copie disponibili non possono essere meno di 0";
    END IF;
END$$

CREATE TRIGGER before_update_libri_copie
BEFORE UPDATE ON libri
FOR EACH ROW
BEGIN
    IF NEW.copie_disponibili < 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = "Errore: le copie disponibili non possono essere meno di 0";
    END IF;
END$$

CREATE TRIGGER before_insert_utenti_email
BEFORE INSERT ON utenti
FOR EACH ROW
BEGIN
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Errore: email non valida";
    END IF;
END$$

CREATE TRIGGER before_update_utenti_email
BEFORE UPDATE ON utenti
FOR EACH ROW
BEGIN
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Errore: email non valida";
    END IF;
END$$

CREATE TRIGGER before_insert_utenti_nome
BEFORE INSERT ON utenti
FOR EACH ROW
BEGIN
    IF NEW.nome NOT REGEXP '^[A-Z][a-z]+ [A-Z][a-z]+$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Nome non valido. Deve contenere Nome e Cognome con iniziali maiuscole';
    END IF;
END$$

CREATE TRIGGER before_update_utenti_nome
BEFORE UPDATE ON utenti
FOR EACH ROW
BEGIN
    IF NEW.nome NOT REGEXP '^[A-Z][a-z]+ [A-Z][a-z]+$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Nome non valido. Deve contenere Nome e Cognome con iniziali maiuscole';
    END IF;
END$$

DELIMITER ;
