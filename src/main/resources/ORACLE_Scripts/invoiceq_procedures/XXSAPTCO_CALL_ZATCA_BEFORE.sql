create or replace PROCEDURE XXSAPTCO_CALL_ZATCA_BEFORE AS
CURSOR C1 IS SELECT SEQ_ID , CUSTOMER_TRX_ID
             FROM   XXSAPTCO_ZATCA_HEADER_ERP
             WHERE  READ_FROM_ERP = 0;
BEGIN
--- Insert DATA in Header and Line table
XXSAPTCO_ZATCA_INTEGRATE_ERP();

---Locked Invoice in AR_CUSTOMER_TRX_ALL
FOR c_header_rec IN C1 LOOP
APPS.XXSAPTCO_ZATCA_PKG.XXSAPTCO_INVOICE_ZATCA_STATUS(c_header_rec.CUSTOMER_TRX_ID,'ZATCA_LOCKED',NULL);

INSERT INTO XXSAPTCO_ZATCA_TRACING_LOG(SEQ,CUSTOMER_TRX_ID,EVENT_DATE,EVENT_TYPE,TRACE_SEQ)
VALUES (c_header_rec.SEQ_id ,c_header_rec.CUSTOMER_TRX_ID,SYSDATE,'Update ERP table to Lock ERP Invoice with status ZATCA_LOCKED',2);

---Update Flag in Header table tp make it as read from ERP
UPDATE XXSAPTCO_ZATCA_HEADER_ERP
SET    READ_FROM_ERP = 1
      ,STATUS        = 'HOST_READY'
WHERE  CUSTOMER_TRX_ID = c_header_rec.CUSTOMER_TRX_ID
AND    SEQ_ID          = c_header_rec.SEQ_ID /*(SELECT MAX(nvl(SEQ_ID,0))
                          FROM   XXSAPTCO_ZATCA_HEADER_ERP
                          WHERE  CUSTOMER_TRX_ID = c_header_rec.CUSTOMER_TRX_ID)*/;

INSERT INTO XXSAPTCO_ZATCA_TRACING_LOG(SEQ,CUSTOMER_TRX_ID,EVENT_DATE,EVENT_TYPE,TRACE_SEQ)
VALUES (c_header_rec.SEQ_id ,c_header_rec.CUSTOMER_TRX_ID,SYSDATE,'Update Invoice Q staging table to Lock ERP Invoice with status ZATCA_LOCKED',3);

COMMIT;
END LOOP;
END;