-- liquibase formatted sql

-- changeset de12650:1 contextFilter:test1 labels:A,B
CREATE TABLE dbo.tab_1 (
	id1 int NOT NULL,
	id2 int NOT NULL,
	val varchar(100) NULL,
	CONSTRAINT tab_1_PK PRIMARY KEY (id1,id2)
);
-- rollback DROP TABLE dbo.tab_1

-- changeset de12650:2 contextFilter:test2 labels:B
CREATE TABLE dbo.tab_2 (	
	id1 int NOT NULL,
	val varchar(100) NULL,
	CONSTRAINT tab_2_PK PRIMARY KEY (id1)
);
-- rollback DROP TABLE dbo.tab_2

