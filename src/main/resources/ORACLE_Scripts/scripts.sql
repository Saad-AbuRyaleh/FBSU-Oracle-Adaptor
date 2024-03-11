CREATE TABLE log_event(
  event_type VARCHAR2(255),
  event_status VARCHAR2(255),
  invoice_number VARCHAR2(255),
  trace_id VARCHAR2(255),
  request VARCHAR2(2000),
  response VARCHAR2(2000),
  formatted_message VARCHAR2(2000),
  event_date_time VARCHAR2(255)
);