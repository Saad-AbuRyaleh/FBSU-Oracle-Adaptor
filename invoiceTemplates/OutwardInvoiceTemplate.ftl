{
"totalNetAmount": "${inv.totalDueAmount?string("0.00")!}",
"totalDiscountAmount":<#if inv.totInvoiceDiscount??>"${inv.totInvoiceDiscount?string("0.00")!0.00}"<#else>"0.00"</#if>,
"totalAdvancedPaidAmount": <#if inv.totalAdvancedAmount??>"${inv.totalAdvancedAmount?string("0.00")!0.00}"<#else>"0.00"</#if>,
"dueDate": "${inv.invoiceQIssueDate!}",
"customerInfo":{
"countryIsoCode": "${transformer.convertIso2CodeToIso3Code(inv.country)}",
"isTaxableCustomer": "true",
"isPerson": <#if inv.invType?? && inv.invType?string == "SIMPLE">true<#else>false</#if>,
"entitySchemeId": <#if inv.customerIdentType??>"${transformer.convertToValidSchemeId(inv.customerIdentType!)}"<#else>null</#if>,
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
},
"supplyDate": "${inv.invoiceQIssueDate!}",
"products": [
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
            "prepaymentInvoiceRef":<#if prepaymentDetail.isHistorical?? && prepaymentDetail.isHistorical?string =="true">"${prepaymentDetail.invoiceId!}"<#else>"${prepaymentDetail.invoiceQReference!}"</#if>
        }<#if prepaymentDetail_has_next>,</#if>
        </#list>
    ],
    </#if>
    "quantity": "${product.quantityInvoiced?string("0.000000000")!}",
    "productCode": "${product.productCode!}",
    "netAmount": "${product.lineAmount?string("0.0000")!}",
    "unitCost": "${product.unitSellingPrice?string("0.00000")!}",
    "taxPercentage": <#if product.taxRate??>"${product.taxRate?string("0")!}"<#else>null</#if>,
    "lineTotalTaxAmount":<#if product.totTax??>"${product.totTax?string("0.0000")!0.00}"<#else>null</#if>,
    "discountAmount": "${product.discount?string("0.0000")!}",
    "description": <#if product.description??>"${product.description?json_string!}"<#else>null</#if>,
    "productName": <#if product.productName??>"${product.productName?json_string!}"<#elseif product.description??>"${product.description?json_string!}"<#else>null</#if>,
    "exemptionPercentage": <#if product.taxRate == 0>100<#else>null</#if>,
    "exemptionReasonCode":<#if product.exemptionCode??>"${product.exemptionCode!}"<#else>null</#if>,
    "exemptionOtherTypeDesc":<#if product.exemptionOtherTypeDesc??>"${product.exemptionOtherTypeDesc!}"<#else>null</#if>
    }<#if product_has_next>,</#if>
</#list>],
"invoiceType": "${inv.invType!}",
"narration": <#if inv.notes??>"${inv.notes?json_string!}"<#else>null</#if>,
"invoiceNumber": "${inv.invoiceId!}",
"currencyIsoCode": "SAR",
"issueDate": "${inv.invoiceQIssueDate!}",
"supplyDate": "${inv.supplyFromZonedDate!}",
"supplyEndDate": "${inv.supplyEndZonedDate!}",
"totalTaxAmount":<#if inv.totalTax??>"${inv.totalTax?string("0.00")!0.00}"<#else>null</#if>,
"totalInvoiceAmount": "${inv.totalAmount?string("0.00")!}"
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