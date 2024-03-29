{
"totalNetAmount": "${inv.totalDueAmount?string("0.00")!}",
"totalDiscountAmount":<#if inv.totInvoiceDiscount??>"${inv.totInvoiceDiscount?string("0.00")!0.00}"<#else>"0.00"</#if>,
"totalAdvancedPaidAmount": <#if inv.totalAdvancedAmount??>"${inv.totalAdvancedAmount?string("0.00")!0.00}"<#else>"0.00"</#if>,
"dueDate": "${inv.invoiceQIssueDate!}",
"customerInfo":<#if inv.invType == "SIMPLE">null<#else>{
"countryIsoCode": "${transformer.convertIso2CodeToIso3Code(inv.country)}",
"isTaxableCustomer": "true",
"isPerson": "false",
"entitySchemeId": <#if inv.custIdentType??>"${transformer.convertToValidSchemeId(inv.custIdentType!)}"<#else>null</#if>,
"entityTaxNumber": "${inv.custVat!}",
"registrationNumber": "${inv.custIdentNumber!}",
"customerCode": "${inv.custNumber!}",
"mode": "UPDATE_IF_EXISTS",
"englishName": <#if inv.custName??>"${inv.custName?json_string!}"<#else>null</#if>,
"arabicName": <#if inv.custNameAr??>"${inv.custNameAr?json_string!}"<#elseif inv.custName??>"${inv.custName?json_string!}"<#else>null</#if>,
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
}</#if>,
"supplyDate": "${inv.invoiceQIssueDate!}",
"products": [
<#list invoiceLines as product>
    {
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
"narration": <#if inv.creditMemoComment??>"${inv.creditMemoComment?json_string!}"<#else>null</#if>,
"invoiceNumber": "${inv.invoiceId!}",
"currencyIsoCode": "SAR",
"issueDate": "${inv.invoiceQIssueDate!}",
"supplyDate": "${inv.supplyFromZonedDate!}",
"supplyEndDate": "${inv.supplyEndZonedDate!}",
"totalTaxAmount":<#if inv.totalTax??>"${inv.totalTax?string("0.00")!0.00}"<#else>null</#if>,
"totalInvoiceAmount": "${inv.totalDueAmount?string("0.00")!}",
"duplicateMode": "RETRIEVE_DUPLICATE_DETAILS",
"invoiceLevelAllowances": [
<#list invoiceLevelAllowances as discount>
    {
    "allowanceCode":"95",
    "amount": <#if discount.amount??>${discount.amount?string("0.0000")!0.00}<#else>null</#if>,
    "taxRate":<#if discount.taxRate??>${discount.taxRate?string("0.00")!0.00}<#else>null</#if>,
    "exemptionCode":<#if discount.exemptionCode??>"${discount.exemptionCode!}"<#else>null</#if>,
    "exemptionOtherTypeDesc":<#if discount.exemptionOtherTypeDesc??>"${discount.exemptionOtherTypeDesc!}"<#else>null</#if>
    }<#if discount_has_next>,</#if>
</#list>]
}