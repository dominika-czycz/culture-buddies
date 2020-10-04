CREATE DATABASE  IF NOT EXISTS `culture_buddies` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `culture_buddies`;
-- MySQL dump 10.13  Distrib 5.7.31, for Linux (x86_64)
--
-- Host: localhost    Database: culture_buddies
-- ------------------------------------------------------
-- Server version	5.7.31-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `addresses` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `city` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `flat_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `street` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `author_book`
--

DROP TABLE IF EXISTS `author_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `author_book` (
                               `book_id` bigint(20) NOT NULL,
                               `author_id` bigint(20) NOT NULL,
                               PRIMARY KEY (`book_id`,`author_id`),
                               KEY `FK7cqs8nb7l859jcwwqoawcokqf` (`author_id`),
                               CONSTRAINT `FK7cqs8nb7l859jcwwqoawcokqf` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`),
                               CONSTRAINT `FKmeehr164a2cpxegeiawuv40a3` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authors` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `first_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `last_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `books` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `identifier` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `thumbnail_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK_e83gawtpnes5cwso26apqdmao` (`identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buddies`
--

DROP TABLE IF EXISTS `buddies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buddies` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `enabled` bit(1) NOT NULL,
                           `last_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `picture` longblob,
                           `username` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `city_id` bigint(20) NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKmcddsioiw0dd7s2wev01y5ln` (`city_id`),
                           CONSTRAINT `FKmcddsioiw0dd7s2wev01y5ln` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buddies_books`
--

DROP TABLE IF EXISTS `buddies_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buddies_books` (
                                 `added` datetime(6) DEFAULT NULL,
                                 `comment` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `rate` int(11) DEFAULT NULL,
                                 `book_id` bigint(20) NOT NULL,
                                 `buddy_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`book_id`,`buddy_id`),
                                 KEY `FK8bwkc8ijtly5w4fcumlmvaof2` (`buddy_id`),
                                 CONSTRAINT `FK8bwkc8ijtly5w4fcumlmvaof2` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`),
                                 CONSTRAINT `FKoil2mo25kgbw15eq7pu8y6sxy` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buddies_relations`
--

DROP TABLE IF EXISTS `buddies_relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buddies_relations` (
                                     `added` datetime(6) DEFAULT NULL,
                                     `buddy_id` bigint(20) NOT NULL,
                                     `buddy_of_id` bigint(20) NOT NULL,
                                     `status_id` bigint(20) DEFAULT NULL,
                                     PRIMARY KEY (`buddy_id`,`buddy_of_id`),
                                     KEY `FKsd3i0mrcukct0dkofqrtjrdty` (`buddy_of_id`),
                                     KEY `FKhrowi4rga48ynk5b5jpw7e4ww` (`status_id`),
                                     CONSTRAINT `FKhrowi4rga48ynk5b5jpw7e4ww` FOREIGN KEY (`status_id`) REFERENCES `relations_status` (`id`),
                                     CONSTRAINT `FKn4991m22xk3fsrp4yl1hugtyk` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`),
                                     CONSTRAINT `FKsd3i0mrcukct0dkofqrtjrdty` FOREIGN KEY (`buddy_of_id`) REFERENCES `buddies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buddies_roles`
--

DROP TABLE IF EXISTS `buddies_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buddies_roles` (
                                 `buddy_id` bigint(20) NOT NULL,
                                 `roles_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`buddy_id`,`roles_id`),
                                 KEY `FK52jx10svvv0mti1w6p97ysdr` (`roles_id`),
                                 CONSTRAINT `FK52jx10svvv0mti1w6p97ysdr` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`),
                                 CONSTRAINT `FKshcij2slauy27xify6fnsqrd8` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cities` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event_types`
--

DROP TABLE IF EXISTS `event_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_types` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `added` datetime(6) DEFAULT NULL,
                          `date` date NOT NULL,
                          `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                          `link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `time` time NOT NULL,
                          `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                          `updated` datetime(6) DEFAULT NULL,
                          `address_id` bigint(20) NOT NULL,
                          `buddy_id` bigint(20) NOT NULL,
                          `event_type_id` bigint(20) NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FKquc7xx27bo60lupj2rf7e0hn2` (`address_id`),
                          KEY `FK51f88h1qgrrwtj6wteunekpxd` (`buddy_id`),
                          KEY `FK2198du56mf1aoaxrdlw3du288` (`event_type_id`),
                          CONSTRAINT `FK2198du56mf1aoaxrdlw3du288` FOREIGN KEY (`event_type_id`) REFERENCES `event_types` (`id`),
                          CONSTRAINT `FK51f88h1qgrrwtj6wteunekpxd` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`),
                          CONSTRAINT `FKquc7xx27bo60lupj2rf7e0hn2` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `events_buddies`
--

DROP TABLE IF EXISTS `events_buddies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events_buddies` (
                                  `event_id` bigint(20) NOT NULL,
                                  `buddy_id` bigint(20) NOT NULL,
                                  PRIMARY KEY (`event_id`,`buddy_id`),
                                  KEY `FK2k7nfebn65436hp5vrnhw40hc` (`buddy_id`),
                                  CONSTRAINT `FK2k7nfebn65436hp5vrnhw40hc` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`),
                                  CONSTRAINT `FKox4rwn8j5e2dix3c2oggospog` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relations_status`
--

DROP TABLE IF EXISTS `relations_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relations_status` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-10-03 21:49:49