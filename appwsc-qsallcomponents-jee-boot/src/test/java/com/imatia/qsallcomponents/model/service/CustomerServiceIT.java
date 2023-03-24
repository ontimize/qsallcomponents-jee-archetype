package com.imatia.qsallcomponents.model.service;


import com.imatia.qsallcomponents.api.services.ICustomerService;
import com.imatia.qsallcomponents.model.dao.CustomerAccountDao;
import com.imatia.qsallcomponents.model.dao.CustomerDao;
import com.imatia.qsallcomponents.model.dao.CustomerTypeDao;
import com.imatia.qsallcomponents.model.dao.dms.*;
import com.ontimize.boot.autoconfigure.dms.ODMSAutoConfigure;
import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.server.services.dms.DMSServiceFileHelper;
import com.ontimize.jee.server.services.dms.DMSServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        CustomerService.class,
        CustomerDao.class,
        CustomerAccountDao.class,
        CustomerTypeDao.class,
        DMSServiceImpl.class,
        DMSServiceFileHelper.class,
        DMSDocumentDao.class,
        DMSDocumentPropertyDao.class,
        DMSRelatedDocumentDao.class,
        DMSCategoryDao.class,
        DMSDocumentFileDao.class,
        DMSDocumentFileVersionDao.class,
        ODMSAutoConfigure.class
})
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test-dms"})
@EnableAutoConfiguration
public class CustomerServiceIT {

    @Autowired
    ICustomerService customerService;

    @Autowired
    DataSource dataSource;

    @Autowired
    IDMSService dmsService;


    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) throws SQLException {

        Connection con = dataSource.getConnection();
        Statement statement = con.createStatement();

        statement.execute("CREATE TABLE TDMS_DOC(ID_DMS_DOC INTEGER IDENTITY NOT NULL PRIMARY KEY,UPDATE_DATE TIMESTAMP,UPDATE_BY_ID INTEGER,DOC_NAME VARCHAR(255) NOT NULL,OWNER_ID INTEGER NOT NULL,DOC_DESCRIPTION CLOB(1G),DOC_KEYWORDS VARCHAR(255))");
        statement.execute("CREATE TABLE TDMS_DOC_FILE(ID_DMS_DOC_FILE INTEGER IDENTITY NOT NULL PRIMARY KEY,FILE_NAME VARCHAR(255) NOT NULL,ID_DMS_DOC INTEGER NOT NULL,FILE_TYPE VARCHAR(255),ID_DMS_DOC_CATEGORY INTEGER)");
        statement.execute("CREATE TABLE TDMS_DOC_FILE_VERSION(ID_DMS_DOC_FILE_VERSION INTEGER IDENTITY NOT NULL PRIMARY KEY,FILE_PATH VARCHAR(500),VERSION INTEGER NOT NULL,FILE_DESCRIPTION CLOB(1G),IS_ACTIVE CHARACTER(1) NOT NULL,FILE_ADDED_DATE TIMESTAMP NOT NULL,FILE_ADDED_USER_ID INTEGER NOT NULL,ID_DMS_DOC_FILE INTEGER NOT NULL,THUMBNAIL BLOB(1G),FILE_SIZE INTEGER)");
        statement.execute("CREATE TABLE TDMS_DOC_PROPERTY(ID_DMS_DOC_PROPERTY INTEGER IDENTITY NOT NULL PRIMARY KEY,DOC_PROPERTY_KEY VARCHAR(255) NOT NULL,DOC_PROPERTY_VALUE VARCHAR(255),ID_DMS_DOC INTEGER NOT NULL)");
        statement.execute("CREATE TABLE TDMS_RELATED_DOC(ID_DMS_RELATED_PROPERTY INTEGER IDENTITY NOT NULL PRIMARY KEY,ID_DMS_DOC_MASTER INTEGER NOT NULL,ID_DMS_DOC_CHILD INTEGER NOT NULL)");
        statement.execute("CREATE TABLE TDMS_DOC_CATEGORY(ID_DMS_DOC_CATEGORY INTEGER IDENTITY NOT NULL PRIMARY KEY,ID_DMS_DOC INTEGER NOT NULL,ID_DMS_DOC_CATEGORY_PARENT INTEGER,CATEGORY_NAME VARCHAR(255) NOT NULL)");


        statement.execute("CREATE TABLE TSERVER_PERMISSION(ID_SERVER_PERMISSION INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY,PERMISSION_NAME VARCHAR(16777216))");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileGetContentOfVersion')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentGetProperty')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileRecoverPreviousVersion')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentDeleteProperties')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentGetProperties')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentGetAllFiles')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/setRelatedDocuments')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentAddProperties')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/getRelatedDocument')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/categoryGetForDocument')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/moveFilesToCategory')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileVersionQuery')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentQuery')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentInsert')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentUpdate')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentGetFiles')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/categoryInsert')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileInsert')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/categoryUpdate')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/categoryDelete')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileDelete')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileGetVersions')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileGetContent')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileUpdate')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/documentDelete')");
        statement.executeUpdate("INSERT INTO TSERVER_PERMISSION (PERMISSION_NAME) VALUES('com.ontimize.jee.server.services.dms.DMSServiceImpl/fileQuery')");


        statement.execute("CREATE TABLE CUSTOMERS(CUSTOMERID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "CUSTOMERTYPEID INTEGER," +
                "NAME VARCHAR(255)," +
                "ADDRESS VARCHAR(255)," +
                "COMMENTS VARCHAR(16777216)," +
                "STARTDATE TIMESTAMP," +
                "PHOTO VARBINARY(16777216)," +
                "SURNAME VARCHAR(255)," +
                "ID VARCHAR(50)," +
                "EMAIL VARCHAR(255)," +
                "COMMENTS_EN_US VARCHAR(16777216)," +
                "COMMENTS_ES_ES VARCHAR(16777216)," +
                "COMMENTS_GL_ES VARCHAR(16777216)," +
                "LONGITUDE DOUBLE," +
                "LATITUDE DOUBLE," +
                "SIGNATURE VARBINARY(16777216)," +
                "ID_DMS_DOC INTEGER," +
                "COUNTRY VARCHAR(50)," +
                "STATE VARCHAR(50)," +
                "ZIPCODE VARCHAR(50)," +
                "PHONE VARCHAR(50))");


        statement.executeUpdate("INSERT INTO CUSTOMERS (CUSTOMERTYPEID, NAME, ADDRESS, COMMENTS, STARTDATE, PHOTO, SURNAME, ID, EMAIL, COMMENTS_EN_US, COMMENTS_ES_ES, COMMENTS_GL_ES, LONGITUDE, LATITUDE, SIGNATURE, ID_DMS_DOC, COUNTRY, STATE, ZIPCODE, PHONE) VALUES(2,'Daisy','49 Lavender Gardens, Battersea, SW11 1DJ ',NULL,'2009-10-06 00:00:00.000000',NULL,'Lawrence','23459862T','Daisy.Lawrence@imatia.com'," +
                "NULL,NULL,NULL,-0.1624365E0,51.4642115E0,NULL,1,'United Kingdom','London',NULL,NULL)");
        statement.executeUpdate("INSERT INTO CUSTOMERS (CUSTOMERTYPEID, NAME, ADDRESS, COMMENTS, STARTDATE, PHOTO, SURNAME, ID, EMAIL, COMMENTS_EN_US, COMMENTS_ES_ES, COMMENTS_GL_ES, LONGITUDE, LATITUDE, SIGNATURE, ID_DMS_DOC, COUNTRY, STATE, ZIPCODE, PHONE) VALUES(3,'James','13, Downing Street','Connected with capital surpluses.','2008-12-23 00:00:00.000000'," +
                "NULL,'Alan','99557548R','james.alan@alan.inc','Comments','Comenttarios para James Alan','Comments',-0.1271615E0,51.5032069E0,NULL,2,'United Kingdom','London',NULL,NULL)");
        statement.executeUpdate("INSERT INTO CUSTOMERS (CUSTOMERTYPEID, NAME, ADDRESS, COMMENTS, STARTDATE, PHOTO, SURNAME, ID, EMAIL, COMMENTS_EN_US, COMMENTS_ES_ES, COMMENTS_GL_ES, LONGITUDE, LATITUDE, SIGNATURE, ID_DMS_DOC, COUNTRY, STATE, ZIPCODE, PHONE) VALUES(3,'Aubrey','Tidorestraat 58-128',NULL,'2010-07-15 00:00:00.000000',NULL,'Engels','75395182D','Aubrey.Engels@ontimize.com',NULL,NULL,NULL," +
                "4.9403621E0,52.3600487E0,NULL,17,'The Netherlands','Amsterdam','1095',NULL)");


        statement.execute("CREATE TABLE CUSTOMERACCOUNTS(CUSTOMERACCOUNTID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "CUSTOMERID INTEGER NOT NULL," +
                "ACCOUNTID INTEGER NOT NULL," +
                "ISOWNER BOOLEAN)");


        statement.executeUpdate("INSERT INTO CUSTOMERACCOUNTS (CUSTOMERID, ACCOUNTID, ISOWNER) VALUES(10602, 1, false)");
        statement.executeUpdate("INSERT INTO CUSTOMERACCOUNTS (CUSTOMERID, ACCOUNTID, ISOWNER) VALUES(10602, 8, true)");
        statement.executeUpdate("INSERT INTO CUSTOMERACCOUNTS (CUSTOMERID, ACCOUNTID, ISOWNER) VALUES(10602, 11, true)");


        statement.execute("CREATE TABLE CUSTOMERTYPES(CUSTOMERTYPEID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "DESCRIPTION VARCHAR(255)," +
                "DESCRIPTION_EN_US VARCHAR(255)," +
                "DESCRIPTION_ES_ES VARCHAR(255)," +
                "DESCRIPTION_GL_ES VARCHAR(255))");

        statement.executeUpdate("INSERT INTO CUSTOMERTYPES VALUES(1,'Normal','Normal','Normal','Normal')");
        statement.executeUpdate("INSERT INTO CUSTOMERTYPES VALUES(2,'VIP','VIP','VIP','VIP')");
        statement.executeUpdate("INSERT INTO CUSTOMERTYPES VALUES(3,'Other','Other','Otro','Outro')");


        statement.execute("CREATE TABLE ACCOUNTS(ACCOUNTID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "ENTITYID VARCHAR(50) NOT NULL," +
                "OFFICEID VARCHAR(50) NOT NULL," +
                "CDID VARCHAR(50)," +
                "ANID VARCHAR(50)," +
                "STARTDATE TIMESTAMP," +
                "ENDDATE TIMESTAMP," +
                "INTERESRATE DOUBLE," +
                "ACCOUNTTYP VARCHAR(255)," +
                "ACCOUNTTYPEID INTEGER)");


        statement.executeUpdate("INSERT INTO ACCOUNTS VALUES(1,'2095','0002','34','0000000010','2018-02-21 22:42:23.248000',NULL,0.009899999999999999,'Savings account',1)");
        statement.executeUpdate("INSERT INTO ACCOUNTS VALUES(2,'2095','0000','00','0000000002','2018-01-16 14:52:22.486000',NULL,0.0125,'Personal account',0)");
        statement.executeUpdate("INSERT INTO ACCOUNTS VALUES(3,'2095','0000','00','0000000003','2018-02-15 06:10:48.576000',NULL,0.043,'Savings account',1)");


        statement.execute("CREATE TABLE MOVEMENTS(MOVEMENTID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "ACCOUNTID INTEGER NOT NULL," +
                "DATE_ TIMESTAMP," +
                "CONCEPT VARCHAR(50)," +
                "MOVEMENTTYPEID INTEGER," +
                "MOVEMENT DOUBLE," +
                "COMMENTS VARCHAR(16777216)," +
                "CONCEPT_EN_US VARCHAR(50)," +
                "CONCEPT_ES_ES VARCHAR(50)," +
                "CONCEPT_GL_ES VARCHAR(50)," +
                "COMMENTS_EN_US VARCHAR(16777216)," +
                "COMMENTS_ES_ES VARCHAR(16777216)," +
                "COMMENTS_GL_ES VARCHAR(16777216))");

        statement.executeUpdate("INSERT INTO MOVEMENTS VALUES(1,1,'2018-01-14 20:19:28.696000','First movement',1,3225.54,NULL,'First movement','Primer movimiento',NULL,NULL,NULL,NULL)");
        statement.executeUpdate("INSERT INTO MOVEMENTS VALUES(2,2,'2018-02-26 16:36:54.205000','Account registration',1,7498.78,NULL,'Account registration','Alta de cuenta',NULL,NULL,NULL,NULL)");
        statement.executeUpdate("INSERT INTO MOVEMENTS VALUES(3,3,'2018-01-23 12:37:38.846000','Open account',1,9461.98,NULL,'Open account','Apertura de cuenta',NULL,NULL,NULL,NULL)");

        statement.executeUpdate("CREATE VIEW VACCOUNTBALANCE AS( SELECT SUM( MOVEMENTS.MOVEMENT) AS BALANCE, ACCOUNTS.ENTITYID + ' ' + ACCOUNTS.OFFICEID + ' ' + ACCOUNTS.CDID + ' ' + ACCOUNTS.ANID AS ACCOUNTNUMBER, ACCOUNTS.ACCOUNTID, ACCOUNTS.ENTITYID, ACCOUNTS.OFFICEID, ACCOUNTS.CDID, ACCOUNTS.ANID, ACCOUNTS.STARTDATE, ACCOUNTS.ENDDATE, ACCOUNTS.INTERESRATE, ACCOUNTS.ACCOUNTTYP FROM ACCOUNTS LEFT JOIN MOVEMENTS ON ACCOUNTS.ACCOUNTID = MOVEMENTS.ACCOUNTID GROUP BY ACCOUNTS.ACCOUNTID )");

    }


    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) throws SQLException {

        Connection con = dataSource.getConnection();
        Statement statement = con.createStatement();

        statement.executeUpdate("DROP VIEW VACCOUNTBALANCE");
        statement.executeUpdate("DROP TABLE CUSTOMERS");
        statement.executeUpdate("DROP TABLE CUSTOMERACCOUNTS");
        statement.executeUpdate("DROP TABLE CUSTOMERTYPES");
        statement.executeUpdate("DROP TABLE TSERVER_PERMISSION");
        statement.executeUpdate("DROP TABLE ACCOUNTS");
        statement.executeUpdate("DROP TABLE MOVEMENTS");

        statement.executeUpdate("DROP TABLE TDMS_DOC");
        statement.executeUpdate("DROP TABLE TDMS_DOC_FILE");
        statement.executeUpdate("DROP TABLE TDMS_DOC_FILE_VERSION");
        statement.executeUpdate("DROP TABLE TDMS_DOC_PROPERTY");
        statement.executeUpdate("DROP TABLE TDMS_RELATED_DOC");
        statement.executeUpdate("DROP TABLE TDMS_DOC_CATEGORY");

    }


    @Nested
    class CustomerCRUD {

        @Test
        void when_customerQuery_receive_keysValues_and_attributes_and_expected_EntityResult_with_PHOTO_without_BytesBlock() {

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERID", 1);

            List<String> attributes = new ArrayList();
            attributes.add("CUSTOMERID");
            attributes.add("CUSTOMERTYPEID");
            attributes.add("NAME");
            attributes.add("PHOTO");
            attributes.add("ADDRESS");

            EntityResult result = customerService.customerQuery(keysValues, attributes);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(1, recordValues.get("CUSTOMERID"));
            assertNotNull(result.get("PHOTO"));

        }

        @Test
        void when_customerPaginationQuery_receive_keysValues_and_attributes_and_recordNumber_and_startIndex_and_orderBy_expected_AdvancedEntityResult() {
            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERID", 1);

            List<String> attributes = new ArrayList();
            attributes.add("CUSTOMERID");
            attributes.add("CUSTOMERTYPEID");
            attributes.add("NAME");
            attributes.add("PHOTO");
            attributes.add("ADDRESS");

            List<Object> orderBy = new ArrayList();
            orderBy.add("NAME");

            AdvancedEntityResult eResult = customerService.customerPaginationQuery(keysValues, attributes, 3, 0, orderBy);
            Map recordValues = eResult.getRecordValues(0);

            assertEquals(1, eResult.calculateRecordNumber());
            assertEquals("James", recordValues.get("NAME"));
            assertEquals(1, recordValues.get("CUSTOMERID"));

            assertNotNull(eResult.get("PHOTO"));

        }

        @Test
        void when_customerInsert_receive_attributes_and_expected_EntityResult() throws DmsException {
            Map<String, Object> attributes = new HashMap<>();

            attributes.put("CUSTOMERTYPEID", 3);
            attributes.put("NAME", "insert");
            attributes.put("ADDRESS", "vigo");
            attributes.put("COMMENTS", null);
            attributes.put("STARTDATE", null);
            attributes.put("PHOTO", null);
            attributes.put("SURNAME", null);
            attributes.put("ID", "23459872T");
            attributes.put("EMAIL", null);
            attributes.put("COMMENTS_EN_US", null);
            attributes.put("COMMENTS_ES_ES", null);
            attributes.put("COMMENTS_GL_ES", null);
            attributes.put("LONGITUDE", null);
            attributes.put("LATITUDE", null);
            attributes.put("SIGNATURE", null);
            attributes.put("ID_DMS_DOC", 67);
            attributes.put("COUNTRY", null);
            attributes.put("STATE", null);
            attributes.put("ZIPCODE", null);
            attributes.put("PHONE", null);

            customerService.customerInsert(attributes);

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("NAME", "insert");

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERID");
            attributesList.add("CUSTOMERTYPEID");
            attributesList.add("NAME");
            attributesList.add("PHOTO");
            attributesList.add("ID_DMS_DOC");

            EntityResult result = customerService.customerQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(3, recordValues.get("CUSTOMERID"));
            assertEquals("insert", recordValues.get("NAME"));

        }

        @Test
        void when_customerUpdate_receive_attributes_and_keysValues_expected_EntityResult() {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("NAME", "Update");

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("CUSTOMERID", 2);

            customerService.customerUpdate(attributes, keysValues);

            keysValues.put("NAME", "Update");

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERID");
            attributesList.add("NAME");


            EntityResult result = customerService.customerQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(2, recordValues.get("CUSTOMERID"));
            assertEquals("Update", recordValues.get("NAME"));


        }

        @Test
        void when_customerDelete_receive_keysValues_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("CUSTOMERID", 3);

            customerService.customerDelete(keysValues);

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERID");
            attributesList.add("CUSTOMERTYPEID");
            attributesList.add("NAME");
            attributesList.add("PHOTO");
            attributesList.add("ID_DMS_DOC");

            EntityResult result = customerService.customerQuery(keysValues, attributesList);
            assertNull(result.getRecordValues(0).get("CUSTOMERID"));
        }
    }

    @Nested
    class CustomerTypeCRUD {

        @Test
        void when_customerTypeQuery_receive_keysValues_and_attributes_and_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERTYPEID", 1);

            List<String> attributes = new ArrayList();
            attributes.add("CUSTOMERTYPEID");
            attributes.add("DESCRIPTION");
            attributes.add("DESCRIPTION_EN_US");
            attributes.add("DESCRIPTION_ES_ES");
            attributes.add("DESCRIPTION_GL_ES");

            EntityResult result = customerService.customerTypeQuery(keysValues, attributes);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(1, recordValues.get("CUSTOMERTYPEID"));
            assertEquals("Normal", recordValues.get("DESCRIPTION"));
        }

        @Test
        void when_customerTypeInsert_receive_attributes_and_expected_EntityResult() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("CUSTOMERTYPEID", 4);
            attributes.put("DESCRIPTION", "cuatro");
            attributes.put("DESCRIPTION_EN_US", "four");
            attributes.put("DESCRIPTION_ES_ES", "cuatro");
            attributes.put("DESCRIPTION_GL_ES", "catro");

            customerService.customerTypeInsert(attributes);

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERTYPEID", 4);

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERTYPEID");
            attributesList.add("DESCRIPTION");
            attributesList.add("DESCRIPTION_EN_US");
            attributesList.add("DESCRIPTION_ES_ES");
            attributesList.add("DESCRIPTION_GL_ES");

            EntityResult result = customerService.customerTypeQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(4, recordValues.get("CUSTOMERTYPEID"));
            assertEquals("cuatro", recordValues.get("DESCRIPTION"));


        }

        @Test
        void when_customerTypeUpdate_receive_attributes_and_keysValues_expected_EntityResult() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("DESCRIPTION", "Update");

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERTYPEID", 3);

            customerService.customerTypeUpdate(attributes, keysValues);

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERTYPEID");
            attributesList.add("DESCRIPTION");
            attributesList.add("DESCRIPTION_EN_US");
            attributesList.add("DESCRIPTION_ES_ES");
            attributesList.add("DESCRIPTION_GL_ES");

            EntityResult result = customerService.customerTypeQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(3, recordValues.get("CUSTOMERTYPEID"));
            assertEquals("Update", recordValues.get("DESCRIPTION"));

        }

        @Test
        void when_customerTypeDelete_receive_keysValues_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERTYPEID", 4);

            customerService.customerTypeDelete(keysValues);

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERTYPEID");
            attributesList.add("DESCRIPTION");
            attributesList.add("DESCRIPTION_EN_US");
            attributesList.add("DESCRIPTION_ES_ES");
            attributesList.add("DESCRIPTION_GL_ES");

            EntityResult result = customerService.customerTypeQuery(keysValues, attributesList);
            assertNull(result.getRecordValues(0).get("CUSTOMERTYPEID"));

        }

        @Test
        void when_customerTypeAggregateQuery_receive_keysValues_and_attributes_and_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap<>();

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERTYPEID");

            EntityResult result = customerService.customerTypeAggregateQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(2, result.calculateRecordNumber());
            assertEquals(2, recordValues.get("CUSTOMERTYPEID"));
            assertNotNull(recordValues);
        }

    }

    @Nested
    class CustomerAccountCRUD {
        @Disabled
        @Test
        void when_customerAccountQuery_receive_keysValues_and_attributes_and_expected_EntityResult() throws SQLException {
            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERACCOUNTID", 1);

            List<String> attributes = new ArrayList();
            attributes.add("CUSTOMERACCOUNTID");
            attributes.add("CUSTOMERID");
            attributes.add("ACCOUNTID");
            attributes.add("ISOWNER");

            Connection con = null;
            try {
                con = dataSource.getConnection();
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMERACCOUNTS, ACCOUNTS, MOVEMENTS, CUSTOMERS");
                while (resultSet.next()) {
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = resultSet.getString(i);
                        System.out.print(resultSet.getMetaData().getColumnName(i) + " → " + columnValue);
                    }
                    System.out.println("");
                }
            } catch (SQLException e) {
            }


            EntityResult result = customerService.customerAccountQuery(keysValues, attributes);

            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(1, recordValues.get("CUSTOMERACCOUNTID"));
            assertEquals(1, recordValues.get("ACCOUNTID"));

            assertNotNull(result.get("ACCOUNTID"));
        }

        @Disabled
        @Test
        void when_customerAccountInsert_receive_attributes_expected_EntityResult() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("CUSTOMERACCOUNTID", 4);
            attributes.put("CUSTOMERID", 10602);
            attributes.put("ACCOUNTID", 8);
            attributes.put("ISOWNER", "FALSE");

            customerService.customerAccountInsert(attributes);

            Map<String, Object> keysValues = new HashMap<>();
            keysValues.put("CUSTOMERACCOUNTID", 4);

            List<String> attributesList = new ArrayList();
            attributesList.add("CUSTOMERACCOUNTID");
            attributesList.add("CUSTOMERID");
            attributesList.add("ACCOUNTID");
            attributesList.add("ISOWNER");

            EntityResult result = customerService.customerAccountQuery(keysValues, attributesList);
            Map recordValues = result.getRecordValues(0);
            assertEquals(1, result.calculateRecordNumber());
            assertEquals(4, recordValues.get("CUSTOMERACCOUNTID"));
            assertEquals(8, recordValues.get("ACCOUNTID"));

        }


    }

}
