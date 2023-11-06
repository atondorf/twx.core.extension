-- liquibase formatted sql

-- changeset de12650:1
CREATE TABLE dbo.test_1 (
	id bigint IDENTITY(0,1) NOT NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL
);
-- rollback DROP TABLE dbo.test_1