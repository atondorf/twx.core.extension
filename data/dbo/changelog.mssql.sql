-- liquibase formatted sql

-- changeset de12650:1
CREATE TABLE dbo.tab_1 (
	id1 int NOT NULL,
	id2 int NOT NULL,
	val varchar(100) NULL,
	CONSTRAINT tab_1_PK PRIMARY KEY (id1,id2)
);
CREATE INDEX tab_1_val_IDX ON dbo.tab_1 (val);
-- rollback DROP DROP INDEX tab_1_val_IDX ON tab_1
-- rollback DROP TABLE dbo.tab_1

