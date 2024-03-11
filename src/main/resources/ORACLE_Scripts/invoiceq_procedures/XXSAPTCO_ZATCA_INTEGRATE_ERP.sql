create or replace PROCEDURE XXSAPTCO_ZATCA_INTEGRATE_ERP AS
V_SEQ NUMBER;
CURSOR  c_header IS
SELECT  ct.customer_trx_id
       ,ct.trx_number invoice_id
       -- ,TO_CHAR(TO_DATE(ct.ATTRIBUTE9, 'YYYY/MM/DD HH24:MI:SS'), 'YYYY-MM-DD') AS issue_date
       -- ,TO_CHAR(TO_DATE(ct.ATTRIBUTE9, 'YYYY/MM/DD HH24:MI:SS'), 'HH24:MI:SS') AS issue_time
      ,TO_CHAR(TO_DATE(
        CASE WHEN nvl(ct.attribute8,'B2C') = 'B2B' THEN TO_CHAR(ct.trx_date,'YYYY/MM/DD HH24:MI:SS') else ct.ATTRIBUTE9 END, 'YYYY/MM/DD HH24:MI:SS'), 'YYYY-MM-DD') AS issue_date
       ,TO_CHAR(TO_DATE(
       CASE WHEN nvl(ct.attribute8,'B2C') = 'B2B' THEN TO_CHAR(ct.trx_date,'YYYY/MM/DD HH24:MI:SS') else ct.ATTRIBUTE9 END, 'YYYY/MM/DD HH24:MI:SS'), 'HH24:MI:SS') AS issue_time
       ,TO_CHAR(TO_DATE(ct.ATTRIBUTE4, 'YYYY/MM/DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS SUPPLY_FROM_DATE
       ,TO_CHAR(TO_DATE(ct.ATTRIBUTE5, 'YYYY/MM/DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS SUPPLY_END_DATE
       ,ct.org_id Organization_unit
       ,ca.account_number customer_number
       ,pa.party_name customer_name
       ,ca.attribute3 customer_vat
       ,ca.attribute2 Cust_Ident_No
       ,ca.attribute4 Cust_Ident_TYPE
       ,loc.country country
       ,loc.city city
       ,loc.postal_code postal_code
       ,ps.attribute1 District
       ,ps.attribute3 Additional_no
       ,ps.attribute4 building_no
       ,loc.address_lines_phonetic Address_street
       ,(SELECT (CASE WHEN a.type ='CM' THEN '381' WHEN a.type ='DM' THEN '383' ELSE '388' END)
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ct.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2) invoice_type,
       (SELECT ABS(SUM(ctlt.extended_amount))
        FROM  ar.ra_customer_trx_lines_all ctlt
        WHERE ct.customer_trx_id = ctlt.customer_trx_id
        AND ctlt.line_type = 'TAX') total_tax ,
       (SELECT ABS(NVL(SUM(ctlt.extended_amount),0))
        FROM ar.ra_customer_trx_lines_all ctlt
        WHERE ct.customer_trx_id = ctlt.customer_trx_id
        AND   ctlt.line_type = 'LINE')total_line ,
      (SELECT ABS(NVL(SUM(ctlt.extended_amount),0))
       FROM ar.ra_customer_trx_lines_all ctlt
       WHERE ct.customer_trx_id = ctlt.customer_trx_id )total_invoice
      ,(select sum(extended_amount)
       from ar.ra_customer_trx_lines_all rctla
       where rctla.customer_trx_id = ct.customer_trx_id
       and line_type = 'LINE'
       and ((nvl(quantity_invoiced,quantity_credited)< 0
       and  (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ct.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2) not in ('CM'))
         OR
         (nvl(quantity_invoiced,quantity_credited)> 0
       and  (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ct.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2)  in ('CM'))))tot_line_discount
       ,(select sum(extended_amount)
         from   ar.ra_customer_trx_lines_all rctla
         where  rctla.customer_trx_id = ct.customer_trx_id
         and    link_to_cust_trx_line_id in (select customer_trx_line_id
                                             from  ar.ra_customer_trx_lines_all ra1
                                             where rctla.customer_trx_id = ra1.customer_trx_id
                                               and ((nvl(quantity_invoiced,quantity_credited)< 0
       and  (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ct.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2) not in ('CM')))
         OR
         ( nvl(quantity_invoiced,quantity_credited)> 0
         and  (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ct.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2)  in ('CM'))))tot_tax_discount
      ,ct.ATTRIBUTE2 credit_memo_comment
      ,(select trx_number from ar.ra_customer_trx_all ctt where  ctt.customer_trx_id = ct.PREVIOUS_CUSTOMER_TRX_ID) credit_memo_no
      ,0    read_from_erp
      ,nvl(ct.attribute8,'B2C')  invoice_buss_type
FROM ar.ra_customer_trx_all ct
,ar.hz_cust_accounts ca
,ar.hz_parties pa
,ar.hz_cust_acct_sites_all cas
,ar.hz_cust_site_uses_all csu
,ar.hz_party_sites ps
,ar.hz_locations loc
,APPS.fnd_territories_vl terr
WHERE /*ct.complete_flag = 'Y'
AND */ct.bill_to_customer_id = ca.cust_account_id
AND ca.party_id = pa.party_id
AND ca.cust_account_id = cas.cust_account_id
AND cas.cust_acct_site_id = csu.cust_acct_site_id
AND ct.bill_to_site_use_id = csu.site_use_id
AND ps.party_site_id = cas.party_site_id
AND ps.location_id = loc.location_id
AND loc.country = terr.territory_code(+)
and ct.attribute6 in ('READY','TECHNICAL_FAILED')
AND CT.CUSTOMER_TRX_ID NOT IN (12534877, 12534872,11541122)
ORDER BY ct.customer_trx_id;

cursor c_line (p_customer_trx_id number)is
SELECT ctl.customer_trx_id ,
       CASE WHEN ctl.previous_customer_trx_line_id IS NOT NULL THEN  TO_NCHAR(ctl.previous_customer_trx_line_id) else TO_NCHAR(ctl.customer_trx_line_id) END AS product_code,
       line_number ,
       ABS(NVL(ctl.quantity_invoiced,ctl.quantity_credited)) quantity_invoiced  ,
       ABS(ctl.extended_amount) extended_amount,
           0  discount ,
       ABS(NVL((SELECT ctlt.extended_amount
             FROM  ar.ra_customer_trx_lines_all ctlt
             WHERE   ctl.customer_trx_id = ctlt.customer_trx_id
             AND ctl.customer_trx_line_id = ctlt.link_to_cust_trx_line_id
             AND ctlt.line_type = 'TAX' ),0))tot_tax ,
          ABS((ctl.extended_amount)  +
                                        ----tax value
       (NVL((SELECT ctlt.extended_amount
             FROM  ar.ra_customer_trx_lines_all ctlt
             WHERE   ctl.customer_trx_id = ctlt.customer_trx_id
             AND ctl.customer_trx_line_id = ctlt.link_to_cust_trx_line_id
             AND ctlt.line_type = 'TAX' ),0)))rounding_amount,
	     ctl.ATTRIBUTE3 description,
	    (SELECT ctlt.tax_rate
         FROM   ar.ra_customer_trx_lines_all ctlt
	     WHERE  ctl.customer_trx_id      = ctlt.customer_trx_id
         AND    ctl.customer_trx_line_id = ctlt.link_to_cust_trx_line_id
         AND    ctlt.line_type           = 'TAX') tax_rate ,
        ABS(ctl.unit_selling_price)unit_selling_price ,
         case when ctl.attribute9 is not null and ctl.attribute8 is null then 'Z'
              when ctl.attribute8 is not null and ctl.attribute9 is null  then 'E'
              when ctl.attribute9 is null and attribute8 is null then 'S'
         end Invoice_cat ,
        ( case
         when nvl(quantity_invoiced,quantity_credited) < 0 and (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ctl.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2) not in ('CM') then 'Y'
          when nvl(quantity_invoiced,quantity_credited) > 0 and (SELECT a.type
         FROM ar.ra_cust_trx_types_all a,ar.ra_customer_trx_all c
         WHERE a.cust_trx_type_id=c.cust_trx_type_id
         AND   ctl.customer_trx_id = c.customer_trx_id
         AND ROWNUM<2)  in ('CM') then 'Y'
         ELSE 'N' end )invoice_type ,
         (case when ctl.attribute9 is not null and ctl.attribute8 is null then attribute9
               when ctl.attribute8 is not null and ctl.attribute9 is null then attribute8
               when ctl.attribute10 is not null and ctl.attribute8 is null and ctl.attribute9 is null then attribute10
         else null end)exemption_code,
         ctl.attribute11 exemption_reason
FROM     ar.ra_customer_trx_lines_all  ctl
WHERE    ctl.customer_trx_id = p_customer_trx_id
AND      ctl.line_type = 'LINE';
BEGIN
FOR c_header_rec IN c_header LOOP
----- insert header invoice
BEGIN
INSERT INTO XXSAPTCO_ZATCA_HEADER_ERP (
CUSTOMER_TRX_ID    ,
INVOICE_ID         ,
ISSUE_DATE         ,
ISSUE_TIME         ,
SUPPLY_FROM_DATE               ,
SUPPLY_END_DATE                ,
ORGANIZATION_UNIT  ,
CUST_NUMBER        ,
CUST_NAME          ,
CUST_VAT           ,
CUST_IDENT_NUMBER  ,
CUST_IDENT_TYPE    ,
COUNTRY            ,
CITY               ,
POSTAL_CODE        ,
DISTRICT           ,
ADDITIONAL_NO      ,
ADDRESS_STREET     ,
INVOICE_TYPE       ,
TOTAL_TAX          ,
TOTAL_AMOUNT       ,
TOTAL_DUE_AMOUNT   ,
TOT_INVOICE_DISCOUNT  ,
CREDIT_MEMO_COMMENT,
CREDIT_MEMO_NO     ,
INVOICE_BUSS_TYPE  ,
BUILDING_NO)
values
(
c_header_rec.CUSTOMER_TRX_ID                ,
c_header_rec.INVOICE_ID                     ,
c_header_rec.ISSUE_DATE                     ,
c_header_rec.ISSUE_TIME                     ,
c_header_rec.SUPPLY_FROM_DATE               ,
c_header_rec.SUPPLY_END_DATE                ,
c_header_rec.ORGANIZATION_UNIT              ,
c_header_rec.CUSTOMER_NUMBER                ,
c_header_rec.CUSTOMER_NAME                  ,
c_header_rec.CUSTOMER_VAT                   ,
c_header_rec.Cust_Ident_No                  ,
c_header_rec.CUST_IDENT_TYPE                ,
c_header_rec.COUNTRY                        ,
c_header_rec.CITY                           ,
c_header_rec.POSTAL_CODE                    ,
c_header_rec.DISTRICT                       ,
c_header_rec.ADDITIONAL_NO                  ,
c_header_rec.ADDRESS_STREET                 ,
c_header_rec.INVOICE_TYPE                   ,
c_header_rec.TOTAL_TAX                      ,
c_header_rec.TOTAL_LINE                     ,
c_header_rec.TOTAL_INVOICE                  ,
c_header_rec.TOT_LINE_DISCOUNT              ,
c_header_rec.CREDIT_MEMO_COMMENT            ,
c_header_rec.CREDIT_MEMO_NO                 ,
c_header_rec.INVOICE_BUSS_TYPE              ,
c_header_rec.building_no                    );
END;

SELECT "INVOICEQ_ADAPTER"."HEAD_ID_SEQ"."CURRVAL"
INTO   V_SEQ
FROM   DUAL;
----- insert line invoice

FOR c_line_rec IN c_line(c_header_rec.CUSTOMER_TRX_ID) LOOP
BEGIN
INSERT INTO XXSAPTCO_ZATCA_LINE_ERP(
SEQ_ID              ,
CUSTOMER_TRX_ID     ,
LINE_NUMBER         ,
QUANTITY_INVOICED   ,
EXTENDED_AMOUNT     ,
DISCOUNT            ,
TOT_TAX             ,
LINE_AMOUNT         ,
PRODUCT_CODE        ,
PRODUCT_DESC        ,
TAX_RATE            ,
UNIT_SELLING_PRICE  ,
INVOICE_CAT         ,
IS_DISCOUNT         ,
EXEMPTION_CODE      ,
EXEMPTION_REASON    )
values
(
V_SEQ                          ,
c_line_rec.CUSTOMER_TRX_ID     ,
c_line_rec.LINE_NUMBER         ,
c_line_rec.QUANTITY_INVOICED   ,
c_line_rec.EXTENDED_AMOUNT     ,
c_line_rec.DISCOUNT            ,
c_line_rec.TOT_TAX             ,
decode(c_line_rec.INVOICE_TYPE,'N',c_line_rec.ROUNDING_AMOUNT,'Y',c_line_rec.EXTENDED_AMOUNT)     ,
c_line_rec.product_code,
c_line_rec.DESCRIPTION         ,
c_line_rec.TAX_RATE            ,
c_line_rec.UNIT_SELLING_PRICE  ,
c_line_rec.INVOICE_CAT         ,
c_line_rec.INVOICE_TYPE        ,
c_line_rec.exemption_code      ,
c_line_rec.exemption_reason
);

END;
END LOOP;

END LOOP;
COMMIT;
END;