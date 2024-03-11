# ORACLE-EBS-ADAPTER
this service is responsible to connect Oracle EBS with invoiceq API.

### To use the service , follow this step:
- You need to add this environment variables before run the service :
```
    INVOICEQ_API_URL -> InvoiceQ external API base url
    INVOICEQ_CONNECTOR_ORGID -> InvoiceQ organization id
    INVOICEQ_CONNECTOR_ORGKEY -> InvoiceQ organization key
    INVOICEQ_CONNECTOR_APIKEY -> API KEY
    DB_CONNECTION_URL -> Database connection url
    DB_NAME -> instance name
    DB_USERNAME -> database username
    DB_PASSWORD -> database password
    SCHEDULER_DELAY -> for example this value "PT5M" make the scheduler read 10 invoices every 5 minutes 
    CONNECTOR_LOG_HOME -> where to save logs, for example: C://Users//user//logs//
    INVOICEQ_API_CONNECTORS_FILE_PATH -> full path of th ejson connectors file 
    CONNECTOR_DATE_TIME_FORMAT -> This is optional , if you deploy in saudi arabia you must set the value as yyyy-MM-dd'T'HH:mm:ss+0300
```
- Add settings.xml file to your .m2 folder (you can find .m2 folder location in your user folder in your pc ).
- in settings.xml you have to add your github username and password (valid token) in settings.xml file ,
# Note (the token must has authority to write and read packages).

### settings.xml file example :

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/invoiceq/Invoiceq-connector</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
    <servers>
        <server>
            <id>github</id>
            <username>USERNAME</username>
            <password>YOUR_ACCESS_TOKEN</password>
        </server>
    </servers>
</settings>

```
- You need to execute the script in the oracle_scripts folder.
- ALTER TABLE xxsaptco_zatca_header_erp ADD supply_from_date VARCHAR2(19);
  ALTER TABLE xxsaptco_zatca_header_erp ADD supply_end_date VARCHAR2(19);
- EXEMPTION_OTHER_DESC