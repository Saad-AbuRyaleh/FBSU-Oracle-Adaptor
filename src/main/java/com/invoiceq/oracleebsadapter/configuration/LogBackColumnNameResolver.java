package com.invoiceq.oracleebsadapter.configuration;

import ch.qos.logback.classic.db.names.DBNameResolver;

import static ch.qos.logback.classic.db.names.ColumnName.*;

public class LogBackColumnNameResolver implements DBNameResolver {
    @Override
    public <N extends Enum<?>> String getTableName(N tableName) {
        return tableName.toString().toLowerCase();
    }

    @Override
    public <N extends Enum<?>> String getColumnName(N columnName) {
        if (ARG0.equals(columnName)) {
            return "event_type";
        } else if (ARG1.equals(columnName)) {
            return "event_status";
        } else if (ARG2.equals(columnName)) {
            return "invoice_number";
        } else if (ARG3.equals(columnName)) {
            return "trace_id";
        }// For other columns, use the default names
        return columnName.name().toLowerCase();
    }
}
