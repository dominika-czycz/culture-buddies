-- MySQL dump 10.13  Distrib 5.7.31, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: culture_buddies
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
-- Dumping data for table `author_book`
--

LOCK TABLES `author_book` WRITE;
/*!40000 ALTER TABLE `author_book` DISABLE KEYS */;
INSERT INTO `author_book` (`book_id`, `author_id`) VALUES (1,1),(2,1),(3,2),(4,3),(5,4),(6,5),(7,6),(8,7),(9,8);
/*!40000 ALTER TABLE `author_book` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `authors`
--

LOCK TABLES `authors` WRITE;
/*!40000 ALTER TABLE `authors` DISABLE KEYS */;
INSERT INTO `authors` (`id`, `first_name`, `last_name`) VALUES (1,'Vladimir','Nabokov'),(2,'Toni','Morrison'),(3,'Mo','Yan'),(4,'Yan','Mo'),(5,'Cormac','McCarthy'),(6,'Gabriel','Márquez'),(7,'Elfriede','Jelinek'),(8,'Michaił','Bułhakow');
/*!40000 ALTER TABLE `authors` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` (`id`, `identifier`, `thumbnail_link`, `title`) VALUES (1,'9780141391601','http://books.google.com/books/content?id=S0lVyYcw8tsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api','Lolita'),(2,'9780141911304','http://books.google.com/books/content?id=SBQgSToNcUsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api','Ada or Ardor'),(3,'030738862X','http://books.google.com/books/content?id=sfmp6gjZGP8C&printsec=frontcover&img=1&zoom=1&source=gbs_api','Beloved'),(4,'9781743771884','http://books.google.com/books/content?id=F8m_DAAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api','The Republic of Wine'),(5,'1559706724','http://books.google.com/books/content?id=fe5wAkv1snoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api','Big Breasts and Wide Hips'),(6,'9781529014587','http://books.google.com/books/content?id=VcZtDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api','The Road'),(7,'8370796338','http://books.google.com/books/content?id=OZTDAAAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api','Sto lat samotności'),(8,'9781847653062','http://books.google.com/books/content?id=d_Ady-4CHRIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api','The Piano Teacher'),(9,'8373161503',NULL,'Mistrz i Małgorzata');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

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
  `picture_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmcddsioiw0dd7s2wev01y5ln` (`city_id`),
  CONSTRAINT `FKmcddsioiw0dd7s2wev01y5ln` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buddies`
--

LOCK TABLES `buddies` WRITE;
/*!40000 ALTER TABLE `buddies` DISABLE KEYS */;
INSERT INTO `buddies` (`id`, `email`, `enabled`, `last_name`, `name`, `password`, `picture_url`, `username`, `city_id`) VALUES (1,'dominika.czycz@gmail.com',_binary '','Kowalska','Anna','$2a$10$vVX7RwazC5r3j7UBDbp.ueoQFxGdL1IQ6tM4cxCTO2Jkeam9lsjUS','annaKowal.jpg','annaKowal',1),(2,'dominika.czycz@gmail.com',_binary '','Mazur','Piotr','$2a$10$VRVE9e/kjG1YazExhTHfzu22iunj0HOyq39JfmX/MTEDH7GbqPJwS','Mazur.jpg','Mazur',1),(3,'dominika.czycz@gmail.com',_binary '','Wojciechowska','Ola','$2a$10$tnvxcyLmxn1DzMwS0lyYRefP/J.jZbkIVlswDrRHOcKo4WbfvQC2y','koala.jpg','koala',1),(4,'dominika.czycz@gmail.com',_binary '','Adamski','Adam','$2a$10$.ky1BsSbu8JORN6Jrh/OrOEBulhnAafIYXXdpxJxZDGSo9eHWk1Oi','adamski.jpg','adamski',1);
/*!40000 ALTER TABLE `buddies` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `buddies_books`
--

LOCK TABLES `buddies_books` WRITE;
/*!40000 ALTER TABLE `buddies_books` DISABLE KEYS */;
INSERT INTO `buddies_books` (`added`, `comment`, `rate`, `book_id`, `buddy_id`) VALUES ('2020-09-25 18:26:01.908163','Pushing the boundaries of what acceptable literature can actually be, Lolita is very much a piece of art. For many years I kept hearing about this book, the content sounding disturbing and perhaps even slightly fascinating. ',10,1,1),('2020-09-25 19:12:57.161878','Sick, twisted and beautiful.Love this. ',9,1,2),('2020-09-25 18:31:57.300062','“Maybe the only thing that hints at a sense of Time is rhythm; not the recurrent beats of the rhythm but the gap between two such beats, the gray gap between black beats: the Tender Interval.” ',10,2,1),('2020-09-25 18:37:31.586196','Just love it ',10,3,1),('2020-09-25 19:21:09.331633','The brutal truth, brilliantly written. A mother hanging from a tree, the vile debasement of a nursing mother, scars so deep from whipping that they make a design of a tree on a woman’s back. ',9,3,3),('2020-09-25 18:41:13.566612','I loved the homage to the female- mother, nurturer, life giver, sacrificial lamb. Yan is brilliant in his use of allegories in the tale of the Shangguan family (specifically Mother and Jintong) from a China (Motherland) violated by war, suffering famine.',10,5,1),('2020-09-25 18:44:34.030713','The Road is a truly disturbing book; it is absorbing, mystifying and completely harrowing. Simply because it shows us how man could act given the right circumstances; it’s a terrifying concept because it could also be a true one.',10,6,1),('2020-09-25 18:47:35.670003','AMAZING THINGS: I can literally feel new wrinkles spreading across the surface of my brain when I read this guy. He\'s so wicked smart that there\'s no chance he\'s completely sane. His adjectives and descriptions are 100% PERFECT, and yet entirely nonsensic',10,7,1),('2020-09-25 19:12:03.877991','good',7,7,2),('2020-09-25 19:20:44.329721','Love it!',10,7,3),('2020-09-25 19:29:28.257238','Amazing',10,7,4),('2020-09-25 19:17:51.809908','This is one of my favorite books. I can\'t even describe how amazed I was when I finished this book. Jelinek moves the reader from character to character, rarely telling us who we inhabit, yet unlike so many other books that abuse this device, it works.',10,8,3),('2020-09-25 19:26:40.548516','Stories, stories, all is stories: political stories, religious stories, scientific stories, even stories about stories. We live inside these stories. Like this one in The Master and Margarita. The story that we can more or less agree upon we call reality.',9,9,4);
/*!40000 ALTER TABLE `buddies_books` ENABLE KEYS */;
UNLOCK TABLES;

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
  KEY `FKs5mevkr15oq0ygottibovc0sf` (`status_id`),
  CONSTRAINT `FKn4991m22xk3fsrp4yl1hugtyk` FOREIGN KEY (`buddy_id`) REFERENCES `buddies` (`id`),
  CONSTRAINT `FKs5mevkr15oq0ygottibovc0sf` FOREIGN KEY (`status_id`) REFERENCES `ralations_status` (`id`),
  CONSTRAINT `FKsd3i0mrcukct0dkofqrtjrdty` FOREIGN KEY (`buddy_of_id`) REFERENCES `buddies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buddies_relations`
--

LOCK TABLES `buddies_relations` WRITE;
/*!40000 ALTER TABLE `buddies_relations` DISABLE KEYS */;
INSERT INTO `buddies_relations` (`added`, `buddy_id`, `buddy_of_id`, `status_id`) VALUES ('2020-09-25 19:11:25.669887',1,2,5),('2020-09-25 19:19:18.214773',1,3,5),('2020-09-25 19:28:35.385837',1,4,5),('2020-09-25 19:11:25.662376',2,1,5),('2020-09-25 19:19:38.483964',2,3,5),('2020-09-25 19:19:18.211187',3,1,5),('2020-09-25 19:19:38.480298',3,2,5),('2020-09-25 19:38:08.485167',3,4,2),('2020-09-25 19:28:35.382843',4,1,5),('2020-09-25 19:38:08.470917',4,3,1);
/*!40000 ALTER TABLE `buddies_relations` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `buddies_roles`
--

LOCK TABLES `buddies_roles` WRITE;
/*!40000 ALTER TABLE `buddies_roles` DISABLE KEYS */;
INSERT INTO `buddies_roles` (`buddy_id`, `roles_id`) VALUES (1,1),(2,1),(3,1),(4,1);
/*!40000 ALTER TABLE `buddies_roles` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `cities`
--

LOCK TABLES `cities` WRITE;
/*!40000 ALTER TABLE `cities` DISABLE KEYS */;
INSERT INTO `cities` (`id`, `name`) VALUES (1,'Wrocław\n'),(2,'Warszawa\n'),(3,'Kraków'),(4,'Poznań'),(5,'Łódź'),(6,'Gdańsk');
/*!40000 ALTER TABLE `cities` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `event_types`
--

LOCK TABLES `event_types` WRITE;
/*!40000 ALTER TABLE `event_types` DISABLE KEYS */;
INSERT INTO `event_types` (`id`, `name`) VALUES (1,'concert'),(2,'theatre'),(3,'literature'),(4,'cinama'),(5,'museum'),(6,'music'),(7,'other');
/*!40000 ALTER TABLE `event_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
-- Table structure for table `addresses`
--


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
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` (`id`, `city`, `flat_number`, `number`, `street`) VALUES (1,'Wrocław','','8a','Włodkowica'),(2,'Wrocław','','19a','Kazimierza Wielkiego '),(3,'Wroclaw','','1','Wystawowa '),(4,'Wrocław','','2a','plac Strzegomski');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;


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
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` (`id`, `added`, `date`, `description`, `link`, `time`, `title`, `updated`, `address_id`, `buddy_id`, `event_type_id`) VALUES (1,'2020-09-25 18:53:34.249441','2020-09-30','Nabokov\'s most controversial books and a lot of wine...','','19:00:00','Wine and Nabokov...',NULL,1,1,3),(2,'2020-09-25 18:58:28.752535','2020-10-09','A tarantino movie marathon and lots of food after that...\r\n','https://www.kinonh.pl/art.s?id=1484','19:00:00','Tarantino',NULL,2,1,4),(3,'2020-09-25 19:10:18.719078','2020-11-22','Orange route of Kult ','https://www.stodola.pl/en/events/kult-akustik-wroclaw-137605.html','18:00:00','Kult',NULL,3,2,1),(4,'2020-09-25 19:35:49.954398','2020-09-30','Understand contemporary art...sometimes it\'s hard, but it\'s worth trying','','14:00:00','Understand contemporary art',NULL,4,4,5);
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `events_buddies`
--

LOCK TABLES `events_buddies` WRITE;
/*!40000 ALTER TABLE `events_buddies` DISABLE KEYS */;
INSERT INTO `events_buddies` (`event_id`, `buddy_id`) VALUES (1,1),(2,1),(1,2),(2,2),(3,2),(1,3),(2,3),(3,4),(4,4);
/*!40000 ALTER TABLE `events_buddies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ralations_status`
--

DROP TABLE IF EXISTS `ralations_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ralations_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ralations_status`
--

LOCK TABLES `ralations_status` WRITE;
/*!40000 ALTER TABLE `ralations_status` DISABLE KEYS */;
INSERT INTO `ralations_status` (`id`, `name`) VALUES (1,'inviting'),(2,'invited'),(3,'blocking'),(4,'blocked'),(5,'buddies');
/*!40000 ALTER TABLE `ralations_status` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`id`, `name`) VALUES (1,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-09-25 21:40:56
