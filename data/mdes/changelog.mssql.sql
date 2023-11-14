-- liquibase formatted sql

-- changeset de12650:1
CREATE SCHEMA mdes
-- rollback DROP SCHEMA mdes

-- changeset de12650:2
CREATE TABLE mdes.tab_1 (
	id1 int NOT NULL,
	id2 int NOT NULL,
	val varchar(100) NULL,
	CONSTRAINT tab_1_PK PRIMARY KEY (id1,id2)
);
CREATE INDEX tab_1_val_IDX ON mdes.tab_1 (val);
-- rollback DROP DROP INDEX tab_1_val_IDX ON mdes.tab_1
-- rollback DROP TABLE mdes.tab_1