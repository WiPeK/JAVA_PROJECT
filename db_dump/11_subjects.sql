--------------------------------------------------------
--  File created - środa-czerwca-21-2017   
--------------------------------------------------------
DROP TABLE "SUBJECTS" cascade constraints;
--------------------------------------------------------
--  DDL for Table SUBJECTS
--------------------------------------------------------

  CREATE TABLE "SUBJECTS" 
   (	"ID_SUBJECT" NUMBER, 
	"NAME" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into JAVA.SUBJECTS
SET DEFINE OFF;
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('1','j. polski');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('2','religia');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('3','j. angielski');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('4','j. rosyjski');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('5','historia');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('6','matematyka');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('7','przyroda');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('8','muzyka');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('9','plastyka');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('10','technika');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('11','wf');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('12','informatyka');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('13','godzina wychowawcza');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('14','fizyka');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('15','chemia');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('40','sgthfhfgh');
Insert into JAVA.SUBJECTS (ID_SUBJECT,NAME) values ('60','dfsghdfhfd');
--------------------------------------------------------
--  DDL for Index SUBJECTS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SUBJECTS_PK" ON "SUBJECTS" ("ID_SUBJECT") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SUBJECTS__UN
--------------------------------------------------------

  CREATE UNIQUE INDEX "SUBJECTS__UN" ON "SUBJECTS" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table SUBJECTS
--------------------------------------------------------

  ALTER TABLE "SUBJECTS" ADD CONSTRAINT "SUBJECTS__UN" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "SUBJECTS" ADD CONSTRAINT "SUBJECTS_PK" PRIMARY KEY ("ID_SUBJECT")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "SUBJECTS" MODIFY ("ID_SUBJECT" NOT NULL ENABLE);