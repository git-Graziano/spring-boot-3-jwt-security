#
# - create tables
#
CREATE TABLE IF NOT EXISTS `spn_user` (
    `ID` bigint NOT NULL AUTO_INCREMENT,
    `FIRST_NAME` varchar(100) NOT NULL,
    `LAST_NAME` varchar(100) NOT NULL,
    `EMAIL` varchar(200) NOT NULL,
    `PASSWORD` varchar(60) NOT NULL,
    UNIQUE KEY `EMAIL` (`EMAIL`),
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `spn_authority` (
    `ID` bigint NOT NULL AUTO_INCREMENT,
    `NAME` varchar(45) NOT NULL,
    `DESCRIPTION` varchar(45) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `spn_user_authority` (
    `USER_ID` bigint NOT NULL,
    `AUTHORITY_ID` bigint NOT NULL,
    PRIMARY KEY (`USER_ID`, `AUTHORITY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `spn_token` (
    `ID` bigint NOT NULL AUTO_INCREMENT,
    `TOKEN` varchar(1024) NOT NULL,
    `TYPE` varchar(20) NOT NULL,
    `ISSUED_AT` datetime(6) NOT NULL,
    `EXPIRED_AT` datetime(6) NOT NULL,
    `ALLOWED` boolean NOT NULL DEFAULT false,
    `USER_ID` bigint NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#
# - index
#
ALTER TABLE `spn_token`
    ADD INDEX `token_user_idx` (`USER_ID` ASC) VISIBLE;

ALTER TABLE `spn_user_authority`
    ADD INDEX `user_authority_authority_idx` (`AUTHORITY_ID` ASC) VISIBLE;

#
# - foreign keys
#
;
ALTER TABLE `spn_token`
    ADD CONSTRAINT `token_user`
        FOREIGN KEY (`USER_ID`)
            REFERENCES `spn_user` (`ID`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE `spn_user_authority`
    ADD CONSTRAINT `user_authority_user`
        FOREIGN KEY (`USER_ID`)
            REFERENCES `spn_user` (`ID`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;


ALTER TABLE `spn_user_authority`
    ADD CONSTRAINT `user_authority_authority`
        FOREIGN KEY (`AUTHORITY_ID`)
            REFERENCES `spn_authority` (`ID`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;



