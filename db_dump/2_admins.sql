--------------------------------------------------------
--  File created - środa-czerwca-21-2017   
--------------------------------------------------------
DROP TABLE "ADMINS" cascade constraints;
DROP TABLE "USERS" cascade constraints;
--------------------------------------------------------
--  DDL for Table ADMINS
--------------------------------------------------------

  CREATE TABLE "ADMINS" 
   (	"ID_ADMIN" NUMBER, 
	"ID_USER" NUMBER, 
	"TITLE" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table USERS
--------------------------------------------------------

  CREATE TABLE "USERS" 
   (	"ID_USER" NUMBER, 
	"EMAIL" VARCHAR2(255 BYTE), 
	"PASSWORD" VARCHAR2(255 BYTE), 
	"NAME" VARCHAR2(50 BYTE), 
	"SURNAME" VARCHAR2(50 BYTE), 
	"PESEL" VARCHAR2(11 BYTE), 
	"CREATE_DATE" DATE, 
	"LAST_LOG_IN" DATE, 
	"LAST_LOG_OUT" DATE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into JAVA.ADMINS
SET DEFINE OFF;
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('22','9','jhg');
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('1','1','tech');
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('2','2','mgr');
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('3','3','mgr inż');
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('4','4','dr');
Insert into JAVA.ADMINS (ID_ADMIN,ID_USER,TITLE) values ('5','5','inż.');
--------------------------------------------------------
--  DDL for Index ADMINS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMINS_PK" ON "ADMINS" ("ID_ADMIN") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ADMINS__UN
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMINS__UN" ON "ADMINS" ("ID_USER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table ADMINS
--------------------------------------------------------

  ALTER TABLE "ADMINS" ADD CONSTRAINT "ADMINS__UN" UNIQUE ("ID_USER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ADMINS" ADD CONSTRAINT "ADMINS_PK" PRIMARY KEY ("ID_ADMIN")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ADMINS" MODIFY ("ID_USER" NOT NULL ENABLE);
  ALTER TABLE "ADMINS" MODIFY ("ID_ADMIN" NOT NULL ENABLE);
