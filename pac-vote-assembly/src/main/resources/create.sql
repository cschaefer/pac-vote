CREATE DATABASE  IF NOT EXISTS `pacvote`;
USE `pacvote`;
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `USER_ID` int(11) NOT NULL,
  `ADMINISTRATOR` bit(1) DEFAULT NULL,
  `PROFILEIMAGEURL` varchar(450) DEFAULT NULL,
  `PROVIDER` varchar(160) DEFAULT NULL,
  `UID` varchar(160) DEFAULT NULL,
  `USERNAME` varchar(160) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `UK_ij8wusyfemkgpeynpl8pv9uh4` (`PROFILEIMAGEURL`),
  UNIQUE KEY `UK_spyxllt2fwt14vyle56pqr1qy` (`UID`),
  UNIQUE KEY `UK_j8pqu7twm7ekoe3orxctxr9f4` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `ballot`;
CREATE TABLE `ballot` (
  `BALLOT_ID` int(11) NOT NULL,
  `active` bit(1) NOT NULL,
  `DESCRIPTION` varchar(160) DEFAULT NULL,
  `lastVoteDate` datetime DEFAULT NULL,
  `QUESTION` varchar(160) NOT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`BALLOT_ID`),
  KEY `FK334yg0i75h7u0d6urarv0of8e` (`USER_ID`),
  CONSTRAINT `FK334yg0i75h7u0d6urarv0of8e` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `choice`;
CREATE TABLE `choice` (
  `CHOICE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ballotId` int(11) DEFAULT NULL,
  `DESCRIPTION` varchar(160) DEFAULT NULL,
  `NAME` varchar(160) NOT NULL,
  `BALLOT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`CHOICE_ID`),
  KEY `FKlcdbhm5ixugah63i6cw8hv9li` (`BALLOT_ID`),
  CONSTRAINT `FKlcdbhm5ixugah63i6cw8hv9li` FOREIGN KEY (`BALLOT_ID`) REFERENCES `ballot` (`BALLOT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `vote`;
CREATE TABLE `vote` (
  `VOTE_ID` int(11) NOT NULL,
  `BALLOT_ID` int(11) DEFAULT NULL,
  `CHOICE_ID` int(11) DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `voteDate` datetime NOT NULL,
  PRIMARY KEY (`VOTE_ID`),
  UNIQUE KEY `UK6dd63guyi8tqpqqfmkm1vctry` (`USER_ID`,`BALLOT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
