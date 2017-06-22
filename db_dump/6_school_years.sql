--------------------------------------------------------
--  File created - środa-czerwca-21-2017   
--------------------------------------------------------
DROP TABLE "SCHOOL_YEARS" cascade constraints;
--------------------------------------------------------
--  DDL for Table SCHOOL_YEARS
--------------------------------------------------------

  CREATE TABLE "SCHOOL_YEARS" 
   (	"ID_SCHOOL_YEAR" NUMBER, 
	"NAME" VARCHAR2(50 BYTE), 
	"START_DATE" DATE, 
	"END_DATE" DATE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into JAVA.SCHOOL_YEARS
SET DEFINE OFF;
Insert into JAVA.SCHOOL_YEARS (ID_SCHOOL_YEAR,NAME,START_DATE,END_DATE) values ('1','ROK SZKOLNY 2013/2014',to_date('13/09/01','RR/MM/DD'),to_date('14/06/30','RR/MM/DD'));
Insert into JAVA.SCHOOL_YEARS (ID_SCHOOL_YEAR,NAME,START_DATE,END_DATE) values ('2','ROK SZKOLNY 2014/2015',to_date('14/09/01','RR/MM/DD'),to_date('15/06/30','RR/MM/DD'));
Insert into JAVA.SCHOOL_YEARS (ID_SCHOOL_YEAR,NAME,START_DATE,END_DATE) values ('3','ROK SZKOLNY 2015/2016',to_date('15/09/01','RR/MM/DD'),to_date('16/06/30','RR/MM/DD'));
Insert into JAVA.SCHOOL_YEARS (ID_SCHOOL_YEAR,NAME,START_DATE,END_DATE) values ('4','ROK SZKOLNY 2016/2017',to_date('16/09/01','RR/MM/DD'),to_date('17/06/30','RR/MM/DD'));
Insert into JAVA.SCHOOL_YEARS (ID_SCHOOL_YEAR,NAME,START_DATE,END_DATE) values ('18','fgdfsgdfgdfg',to_date('17/05/30','RR/MM/DD'),to_date('17/06/06','RR/MM/DD'));
--------------------------------------------------------
--  DDL for Index SCHOOL_YEARS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SCHOOL_YEARS_PK" ON "SCHOOL_YEARS" ("ID_SCHOOL_YEAR") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table SCHOOL_YEARS
--------------------------------------------------------

  ALTER TABLE "SCHOOL_YEARS" ADD CONSTRAINT "SCHOOL_YEARS_PK" PRIMARY KEY ("ID_SCHOOL_YEAR")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "SCHOOL_YEARS" MODIFY ("ID_SCHOOL_YEAR" NOT NULL ENABLE);