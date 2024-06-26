{
<#if isGroupReference?? && isGroupReference?c =="true">
    "groupedInvoiceIQReferences": [
    <#list groupedInvoiceIQReferences?split(",") as reference>
        "${reference}"<#if reference_has_next>,</#if>
    </#list>
    ],
</#if>
"invoiceIQReference":"${invoiceQReference!}",
"debitNoteNumber": "${inv.invoiceId!}",
"isHistorical": "${inv.isHistorical?c!}",
"historicalInvoiceType":<#if inv.isHistorical && inv.invType??>"${inv.invType!}"<#else>null</#if>,
"historicalCurrencyIsoCode": "SAR",
"totalDebitedNetAmount": "${inv.totalDueAmount?string("0.00")!}",
"totalDebitedDiscountAmount":<#if inv.totInvoiceDiscount??>"${inv.totInvoiceDiscount?string("0.00")!0.00}"<#else>"0.00"</#if>,
"totalTaxAmount":<#if inv.totalTax??>"${inv.totalTax?string("0.00")!0.00}"<#else>null</#if>,
"issueDate": "${inv.invoiceQIssueDate!}",
"supplyDate": "${inv.supplyFromZonedDate!}",
"supplyEndDate": "${inv.supplyEndZonedDate!}",
"historicalCustomerInfo":<#if inv.isHistorical && inv.invType??>{
    "countryIsoCode": "${transformer.convertIso2CodeToIso3Code(inv.country!)}",
    "isTaxableCustomer": "true",
    "isPerson": <#if inv.invType?? && inv.invType?string == "SIMPLE">true<#else>false</#if>,
    "entitySchemeId":<#if inv.customerIdentType??> "${inv.customerIdentType!}"<#else>null</#if>,
    "entityTaxNumber": "${inv.customerVat!}",
    "registrationNumber": "${inv.customerIdentNumber!}",
    "nationalNumber": "${inv.customerIdentNumber!}",
    "customerCode": "${inv.customerNumber!}",
    "mode": "UPDATE_IF_EXISTS",
    "englishName": <#if inv.customerName??>"${inv.customerName?json_string!}"<#else>null</#if>,
    "arabicName": <#if inv.customerNameAr??>"${inv.customerNameAr?json_string!}"<#elseif inv.customerName??>"${inv.customerName?json_string!}"<#else>null</#if>,
    "contactPersonName": null,
    "contactPersonMobile": null,
    "mobile": null,
    "preferredChannel": "NO_NOTIFICATION",
    "email": null,
    "ccEmail": null,
    "city": <#if inv.city??>"${inv.city?json_string!}"<#else>null</#if>,
    "area": <#if inv.district??>"${inv.district?json_string!}"<#else>null</#if>,
    "street": <#if inv.addressStreet??>"${inv.addressStreet?json_string!}"<#else>null</#if>,
    "bldgNo": <#if inv.buildingNo??>"${inv.buildingNo?json_string!}"<#else>null</#if>,
    "pobox": <#if inv.postalCode??>"${inv.postalCode?json_string!}"<#else>null</#if>
}
<#else>null</#if>,
"debitProducts":[
<#list invoiceLines as product>
{
    <#if product.prepaymentTaxableAmount?? && isPrepaymentTaxableAmountValid(product)>
        "prepaymentDetails": [
        <#list product.prepaymentDetails as prepaymentDetail>
            {
            "invoiceDate": "${prepaymentDetail.prePaymentInvoiceDate!}",
            "isHistorical": <#if prepaymentDetail.isHistorical?? && prepaymentDetail.isHistorical?string =="true">"true"<#else>"false"</#if>,
            "prePaymentTaxAmount": "${prepaymentDetail.prepaymentTaxAmount?string("0.0000")!}",
            "prePaymentTaxableAmount": "${prepaymentDetail.prepaymentTaxableAmount?string("0.0000")!}",
            "prepaymentInvoiceRef":<#if prepaymentDetail.isHistorical?? && prepaymentDetail.isHistorical?string =="true">"${prepaymentDetail.invoiceId!}"<#else>"${prepaymentDetail.invoiceQReference!}"</#if>,
            "exemptionPercentage": <#if prepaymentDetail.prePaymentTaxRate == 0>100<#else>null</#if>,
            "exemptionCode":<#if prepaymentDetail.exemptionReasonCode??>"${prepaymentDetail.exemptionReasonCode!}"<#else>null</#if>,
            "exemptionOtherTypeDesc":<#if prepaymentDetail.exemptionOtherTypeDesc??>"${prepaymentDetail.exemptionOtherTypeDesc!}"<#else>null</#if>,
            "taxRate": <#if prepaymentDetail.prePaymentTaxRate??>"${prepaymentDetail.prePaymentTaxRate?string("0")!}"<#else>null</#if>
            }<#if prepaymentDetail_has_next>,</#if>
        </#list>
        ],
    </#if>
    "debitDiscountAmount": "${product.discount?string("0.0000")!}",
    "debitedNetAmount":  "${product.lineAmount?string("0.0000")!}",
    "debitedQuantity": "${product.quantityInvoiced?string("0.000000000")!}",
    "debitedTaxAmount": <#if product.totTax??>"${product.totTax?string("0.0000")!0.00}"<#else>null</#if>,
    "debitedUnitPrice": "${product.unitSellingPrice?string("0.00000")!}",
    "taxPercentage":<#if product.taxRate??>"${product.taxRate?string("0")!}"<#else>null</#if>,
    "productName": <#if product.productName??>"${product.productName?json_string!}"<#elseif product.description??>"${product.description?json_string!}"<#else>null</#if>,
    "description": <#if product.description??>"${product.description?json_string!}"<#else>null</#if>,
    "productCode": "${cleanNumber(product_index+1)}_${product.productCode!}",
    "exemptionPercentage":  <#if product.taxRate?? && product.taxRate == 0>100<#else>null</#if>,
    "exemptionReasonCode":<#if product.exemptionCode??>"${product.exemptionCode!}"<#else>null</#if>,
    "exemptionOtherTypeDesc":<#if product.exemptionOtherTypeDesc??>"${product.exemptionOtherTypeDesc!}"<#else>null</#if>,
    "historicalInvoiceLine":<#if inv.isHistorical>{
        "productName": <#if product.productName??>"${product.productName?json_string!}"<#elseif product.description??>"${product.description?json_string!}"<#else>null</#if>,
        "invoicedLineTotalAmountWithTax" :"${product.lineAmount?string("0.0000")!}",
        "invoicedLineTotalTaxAmount": <#if product.totTax??>"${product.totTax?string("0.0000")!0.00}"<#else>null</#if>,
        "invoicedTaxPercentage":<#if product.taxRate??>"${product.taxRate?string("0")!}"<#else>null</#if>,
        "invoicedUnitCost": "${product.unitSellingPrice?string("0.00000")!}",
        "exemptionPercentage":  <#if product.taxRate?? && product.taxRate == 0>100<#else>null</#if>,
        "exemptionReasonCode":<#if product.exemptionCode??>"${product.exemptionCode!}"<#else>null</#if>,
        "exemptionOtherTypeDesc":<#if product.exemptionOtherTypeDesc??>"${product.exemptionOtherTypeDesc!}"<#else>null</#if>
    }<#else>null</#if>
}
<#if product_has_next>,</#if>
</#list>],
"debitReason": <#if inv.memoComment??>"${inv.memoComment?json_string!}"<#else>null</#if>,
"narration": <#if inv.notes??>"${inv.notes?json_string!}"<#else>null</#if>
}
<#function isPrepaymentTaxableAmountValid product>
    <#if
    product.prepaymentTaxableAmount?string("0.0000") != "0.0000">
        <#return true>
    <#else>
        <#return false>
    </#if>
</#function>
<#function cleanNumber input>
    <#return input?replace("@", "")>
</#function>