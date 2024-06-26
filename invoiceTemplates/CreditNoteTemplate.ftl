{
<#if isGroupReference?? && isGroupReference?c =="true">
    "groupedInvoiceIQReferences": [
    <#list groupedInvoiceIQReferences?split(",") as reference>
        "${reference}"<#if reference_has_next>,</#if>
    </#list>
    ],
</#if>
"invoiceIQReference":"${invoiceQReference!}",
"creditNoteNumber": "${inv.invoiceId!}",
"creditNoteMode":"AMOUNT",
"isHistorical": "${inv.isHistorical?c!}",
"historicalInvoiceType":<#if inv.isHistorical && inv.invType??>"${inv.invType!}"<#else>null</#if>,
"historicalCurrencyIsoCode": "SAR",
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
"creditProducts":[
<#list invoiceLines as product>
{
    "creditDiscountAmount": "${product.discount?string("0.0000")!}",
    "creditedNetAmount":  "${product.lineAmount?string("0.0000")!}",
    "creditedQuantity": "${product.quantityInvoiced?string("0.0000")!}",
    "creditedTaxAmount": <#if product.totTax??>"${product.totTax?string("0.0000")!0.00}"<#else>null</#if>,
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
"creditReason": <#if inv.memoComment??>"${inv.memoComment?json_string!}"<#else>null</#if>,
"narration": <#if inv.notes??>"${inv.notes?json_string!}"<#else>null</#if>
}
<#function cleanNumber input>
    <#return input?replace("@", "")>
</#function>