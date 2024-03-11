--------------------------------------------------------
--  DDL for Table XXSAPTCO_ZATCA_HEADER_ERP
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" 
   (	"SEQ_ID" NUMBER DEFAULT "INVOICEQ_ADAPTER"."HEAD_ID_SEQ"."NEXTVAL", 
	"CUSTOMER_TRX_ID" NUMBER(15,0), 
	"INVOICE_ID" VARCHAR2(20 BYTE), 
	"ISSUE_DATE" VARCHAR2(10 BYTE), 
	"ISSUE_TIME" VARCHAR2(32 BYTE), 
	"ORGANIZATION_UNIT" NUMBER(15,0), 
	"CUST_NUMBER" VARCHAR2(30 BYTE), 
	"CUST_NAME" VARCHAR2(4000 BYTE), 
	"CUST_VAT" VARCHAR2(50 BYTE), 
	"CUST_IDENT_NUMBER" VARCHAR2(4000 BYTE), 
	"CUST_IDENT_TYPE" VARCHAR2(20 BYTE), 
	"COUNTRY" VARCHAR2(1800 BYTE), 
	"CITY" VARCHAR2(1800 BYTE), 
	"POSTAL_CODE" VARCHAR2(600 BYTE), 
	"DISTRICT" VARCHAR2(4000 BYTE), 
	"ADDITIONAL_NO" VARCHAR2(4000 BYTE), 
	"ADDRESS_STREET" VARCHAR2(4000 BYTE), 
	"INVOICE_TYPE" VARCHAR2(3 BYTE), 
	"TOTAL_TAX" NUMBER, 
	"TOTAL_AMOUNT" NUMBER, 
	"TOTAL_DUE_AMOUNT" NUMBER, 
	"TOT_INVOICE_DISCOUNT" NUMBER, 
	"CREDIT_MEMO_COMMENT" VARCHAR2(4000 BYTE), 
	"CREDIT_MEMO_NO" VARCHAR2(4000 BYTE), 
	"READ_FROM_ERP" NUMBER DEFAULT 0, 
	"STATUS" VARCHAR2(20 BYTE), 
	"REFERENCE" VARCHAR2(2000 BYTE), 
	"UUID" VARCHAR2(60 BYTE), 
	"SIGNATURE" CLOB, 
	"INVOICE_BUSS_TYPE" VARCHAR2(5 BYTE), 
	"ZATCA_RESPONSE" VARCHAR2(4000 BYTE), 
	"ERROR_DETAILS" CLOB, 
	"PAYABLE_ROUNDING_AMOUNT" NUMBER(19,2), 
	"TOTAL_ADVANCED_AMOUNT" NUMBER(19,2), 
	"CUST_NAME_AR" VARCHAR2(255 CHAR), 
	"BUILDING_NO" VARCHAR2(50 BYTE), 
	"SUPPLY_FROM_DATE" VARCHAR2(19 BYTE), 
	"SUPPLY_END_DATE" VARCHAR2(19 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" 
 LOB ("SIGNATURE") STORE AS SECUREFILE (
  TABLESPACE "APPS_TS_TX_DATA" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) 
 LOB ("ERROR_DETAILS") STORE AS SECUREFILE (
  TABLESPACE "APPS_TS_TX_DATA" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
--------------------------------------------------------
--  DDL for Index SYS_C00879106
--------------------------------------------------------

  CREATE UNIQUE INDEX "INVOICEQ_ADAPTER"."SYS_C00879106" ON "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" ("SEQ_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;
--------------------------------------------------------
--  Constraints for Table XXSAPTCO_ZATCA_HEADER_ERP
--------------------------------------------------------

  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" MODIFY ("CUSTOMER_TRX_ID" NOT NULL ENABLE);
  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" MODIFY ("INVOICE_ID" NOT NULL ENABLE);
  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" MODIFY ("CUST_NUMBER" NOT NULL ENABLE);
  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" ADD PRIMARY KEY ("SEQ_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA"  ENABLE;

--------------------------------------------------------
--  DDL for Table XXSAPTCO_ZATCA_LINE_ERP
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_LINE_ERP"
   (	"SEQ_ID" NUMBER,
	"CUSTOMER_TRX_ID" NUMBER(15,0),
	"LINE_NUMBER" NUMBER,
	"QUANTITY_INVOICED" NUMBER,
	"EXTENDED_AMOUNT" NUMBER,
	"DISCOUNT" NUMBER,
	"TOT_TAX" NUMBER,
	"LINE_AMOUNT" NUMBER,
	"PRODUCT_CODE" VARCHAR2(4000 BYTE),
	"PRODUCT_DESC" VARCHAR2(4000 BYTE),
	"TAX_RATE" NUMBER,
	"UNIT_SELLING_PRICE" NUMBER,
	"INVOICE_CAT" VARCHAR2(4 BYTE),
	"EXEMPTION_CODE" VARCHAR2(30 BYTE),
	"EXEMPTION_REASON" VARCHAR2(4000 BYTE),
	"IS_DISCOUNT" VARCHAR2(2 BYTE) DEFAULT 'N',
	"DESCRIPTION" VARCHAR2(255 CHAR),
	"EXEMPTION_PERCENTAGE" NUMBER(19,2),
	"PRODUCT_NAME" VARCHAR2(255 CHAR),
	"ID" NUMBER DEFAULT "INVOICEQ_ADAPTER"."HEAD_ID_SEQ"."NEXTVAL"
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;
--------------------------------------------------------
--  Constraints for Table XXSAPTCO_ZATCA_LINE_ERP
--------------------------------------------------------

  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_LINE_ERP" MODIFY ("CUSTOMER_TRX_ID" NOT NULL ENABLE);
  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_LINE_ERP" MODIFY ("LINE_NUMBER" NOT NULL ENABLE);
  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_LINE_ERP" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table XXSAPTCO_ZATCA_LINE_ERP
--------------------------------------------------------

  ALTER TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_LINE_ERP" ADD FOREIGN KEY ("SEQ_ID")
	  REFERENCES "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_HEADER_ERP" ("SEQ_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table XXSAPTCO_ZATCA_TRACING_LOG
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."XXSAPTCO_ZATCA_TRACING_LOG"
   (	"SEQ" NUMBER,
	"CUSTOMER_TRX_ID" NUMBER,
	"EVENT_DATE" DATE,
	"EVENT_TYPE" VARCHAR2(200 BYTE),
	"TRACE_SEQ" NUMBER
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;

--------------------------------------------------------
--  DDL for Table XX_AR_EREP_INV_LOBS
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS"
   (	"EREP_LOB_ID" NUMBER DEFAULT "INVOICEQ_ADAPTER"."LOBS_SEQ"."NEXTVAL",
	"BLOB_FILE_CONTENT" BLOB,
	"BLOB_FILE_NAME" VARCHAR2(255 BYTE),
	"CUSTOMER_TRX_ID" NUMBER,
	"CREATED_BY" NUMBER,
	"CREATED_ON" TIMESTAMP (6),
	"ERP_FILE_STATUS" VARCHAR2(100 BYTE) DEFAULT 'PENDING'
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA"
 LOB ("BLOB_FILE_CONTENT") STORE AS SECUREFILE (
  TABLESPACE "APPS_TS_TX_DATA" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
  GRANT UPDATE ON "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" TO "APPS";
  GRANT SELECT ON "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" TO "APPS";
  GRANT INSERT ON "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" TO "APPS";
  GRANT DELETE ON "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" TO "APPS";
--------------------------------------------------------
--  DDL for Index ERP_BLOBFILE_EBS_PK_1
--------------------------------------------------------

  CREATE UNIQUE INDEX "INVOICEQ_ADAPTER"."ERP_BLOBFILE_EBS_PK_1" ON "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" ("EREP_LOB_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;
--------------------------------------------------------
--  Constraints for Table XX_AR_EREP_INV_LOBS
--------------------------------------------------------

  ALTER TABLE "INVOICEQ_ADAPTER"."XX_AR_EREP_INV_LOBS" ADD CONSTRAINT "ERP_BLOBFILE_EBS_PK_1" PRIMARY KEY ("EREP_LOB_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA"  ENABLE;

--------------------------------------------------------
--  DDL for Table NODE_LOGGER
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."NODE_LOGGER"
   (	"ID" NUMBER,
	"LAST_ACTIVE_ON" TIMESTAMP (6),
	"SERVICE_CODE" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C00879102
--------------------------------------------------------

  CREATE UNIQUE INDEX "INVOICEQ_ADAPTER"."SYS_C00879102" ON "INVOICEQ_ADAPTER"."NODE_LOGGER" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA" ;
--------------------------------------------------------
--  Constraints for Table NODE_LOGGER
--------------------------------------------------------

  ALTER TABLE "INVOICEQ_ADAPTER"."NODE_LOGGER" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "APPS_TS_TX_DATA"  ENABLE;

--------------------------------------------------------
--  DDL for Table LOG_EVENT
--------------------------------------------------------

  CREATE TABLE "INVOICEQ_ADAPTER"."LOG_EVENT"
   (	"EVENT_TYPE" VARCHAR2(255 BYTE),
	"EVENT_STATUS" VARCHAR2(255 BYTE),
	"INVOICE_NUMBER" VARCHAR2(255 BYTE),
	"TRACE_ID" VARCHAR2(255 BYTE),
	"REQUEST" VARCHAR2(2000 BYTE),
	"RESPONSE" VARCHAR2(2000 BYTE),
	"FORMATTED_MESSAGE" VARCHAR2(2000 BYTE),
	"EVENT_DATE_TIME" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "APPS_TS_TX_DATA" ;
