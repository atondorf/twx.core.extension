-- liquibase formatted sql

-- changeset de12650:1 
CREATE TABLE dbo.tab_1 (
	id bigint IDENTITY(0,1) NOT NULL,
	valBool bit NULL,
	valTinyInt tinyint NULL,
	valSmallInt smallint NULL,
	valInt int NULL,
	valBigInt bigint NULL,
	valReal real NULL,
	valFloat float NULL,
	valDev decimal NULL,
	vaDateTime datetime2 NULL,
	valFixStr nchar(255) NULL,
	valStr nvarchar(MAX) NULL,
	valFixBinary binary(255) NULL,
	valBinary varbinary(MAX) NULL,
	valImage varbinary(MAX) NULL,
	valJSON nvarchar(MAX) NULL,
	valXML xml NULL,
	CONSTRAINT tab_1_PK PRIMARY KEY (id)
)
-- rollback DROP TABLE dbo.tab_1

-- changeset de12650:2 
INSERT INTO dbo.tab_1 (valBool,valTinyInt,vaDateTime) VALUES (0,1,'2023-12-20T18:00:00Z')
INSERT INTO dbo.tab_1 (valBool,valTinyInt) VALUES (1,2)
-- rollback TRUNCATE TABLE dbo.tab_1

