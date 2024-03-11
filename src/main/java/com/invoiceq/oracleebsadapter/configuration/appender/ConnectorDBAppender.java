package com.invoiceq.oracleebsadapter.configuration.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConnectorDBAppender extends DBAppenderBase<ILoggingEvent> {
    protected String insertSQL;


    @Override
    public void start() {
        insertSQL = buildInsertSQL();
        super.start();
    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return null;
    }

    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }

    @Override
    protected void subAppend(ILoggingEvent eventObject, Connection connection, PreparedStatement statement) throws Throwable {
        bindLoggingEventWithInsertStatement(statement, eventObject);
        statement.execute();
    }

    private void bindLoggingEventWithInsertStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        int length = event.getArgumentArray().length;
        for (int i = 0; i < 6; i++) {
            if (i < length) {
                stmt.setString(i + 1, asStringTruncatedTo254(event.getArgumentArray()[i]));
            } else {
                stmt.setString(i + 1, null);
            }
        }
        stmt.setString(7, event.getFormattedMessage());
        stmt.setString(8, ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    String asStringTruncatedTo254(Object o) {
        String s = null;
        if (o != null) {
            s = o.toString();
        }

        if (s == null) {
            return null;
        }
        if (s.length() <= 254) {
            return s;
        } else {
            return s.substring(0, 254);
        }
    }

    @Override
    protected void secondarySubAppend(ILoggingEvent eventObject, Connection connection, long eventId) throws Throwable {

    }

    static String buildInsertSQL() {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append("log_events").append(" (");
        sqlBuilder.append("event_type").append(", ");
        sqlBuilder.append("event_status").append(", ");
        sqlBuilder.append("invoice_number").append(", ");
        sqlBuilder.append("trace_id").append(", ");
        sqlBuilder.append("request").append(", ");
        sqlBuilder.append("response").append(", ");
        sqlBuilder.append("formatted_message").append(", ");
        sqlBuilder.append("event_date_time").append(") ");
        sqlBuilder.append("VALUES (?, ?, ? ,?, ?, ?, ?, ?)");
        return sqlBuilder.toString();
    }
}
