-- liquibase formatted sql

-- changeset de12650:1 
CREATE TABLE dbo.tab_1 (
	id1 int NOT NULL,
	id2 int NOT NULL,
	tab_2_id int,
	val varchar(100) NULL,
	CONSTRAINT tab_1_PK PRIMARY KEY (id1,id2)
);
CREATE INDEX tab_1_tab_2_id_IDX ON twdata.dbo.tab_1 (tab_2_id);
-- rollback DROP INDEX dbo.tab_1.tab_1_tab_2_id_IDX
-- rollback DROP TABLE dbo.tab_1

-- changeset de12650:2 
CREATE TABLE dbo.tab_2 (	
	id1 int NOT NULL,
	val varchar(100) NULL,
	CONSTRAINT tab_2_PK PRIMARY KEY (id1)
);
-- rollback DROP TABLE dbo.tab_2

-- changeset de12650:3 
ALTER TABLE twdata.dbo.tab_1 ADD CONSTRAINT tab_1_FK FOREIGN KEY (tab_2_id) REFERENCES twdata.dbo.tab_2(id1);
-- rollback ALTER TABLE twdata.dbo.tab_1 DROP CONSTRAINT tab_1_FK
