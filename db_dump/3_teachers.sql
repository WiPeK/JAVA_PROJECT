--------------------------------------------------------
--  File created - środa-czerwca-21-2017   
--------------------------------------------------------
DROP TABLE "TEACHERS" cascade constraints;
DROP TABLE "USERS" cascade constraints;
--------------------------------------------------------
--  DDL for Table TEACHERS
--------------------------------------------------------

  CREATE TABLE "TEACHERS" 
   (	"ID_TEACHER" NUMBER, 
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
REM INSERTING into JAVA.TEACHERS
SET DEFINE OFF;
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('55','1215','eee');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('1','50','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('2','51','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('3','52','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('4','53','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('5','54','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('6','55','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('7','56','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('8','57','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('9','58','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('10','59','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('11','60','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('12','61','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('13','62','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('14','63','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('15','64','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('16','65','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('17','66','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('18','67','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('19','68','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('20','69','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('21','70','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('22','71','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('23','72','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('24','73','dr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('25','74','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('26','75','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('27','76','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('28','77','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('29','78','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('30','79','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('31','80','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('32','81','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('33','82','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('34','83','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('35','84','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('36','85','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('37','86','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('38','87','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('39','88','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('40','89','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('41','90','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('42','91','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('43','92','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('44','93','brak');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('45','94','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('46','95','mgr');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('47','96','mgr inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('48','97','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('49','98','inż.');
Insert into JAVA.TEACHERS (ID_TEACHER,ID_USER,TITLE) values ('50','99','brak');
--------------------------------------------------------
--  DDL for Index TEACHERS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TEACHERS_PK" ON "TEACHERS" ("ID_TEACHER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index TEACHERS__UN
--------------------------------------------------------

  CREATE UNIQUE INDEX "TEACHERS__UN" ON "TEACHERS" ("ID_USER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table TEACHERS
--------------------------------------------------------

  ALTER TABLE "TEACHERS" ADD CONSTRAINT "TEACHERS__UN" UNIQUE ("ID_USER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "TEACHERS" ADD CONSTRAINT "TEACHERS_PK" PRIMARY KEY ("ID_TEACHER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "TEACHERS" MODIFY ("ID_USER" NOT NULL ENABLE);
  ALTER TABLE "TEACHERS" MODIFY ("ID_TEACHER" NOT NULL ENABLE);
