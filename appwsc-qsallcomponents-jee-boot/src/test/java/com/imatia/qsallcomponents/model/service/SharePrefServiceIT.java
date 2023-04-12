package com.imatia.qsallcomponents.model.service;


import com.imatia.qsallcomponents.api.services.ISharePrefService;
import com.imatia.qsallcomponents.model.dao.setup.OCSharePreferences;
import com.imatia.qsallcomponents.model.dao.setup.OCSharePreferencesTarget;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.DMSCreationHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DMSCreationHelper.class,
        SharePrefService.class,
        OCSharePreferences.class,
        OCSharePreferencesTarget.class})
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
class SharePrefServiceIT {

    @Autowired
    ISharePrefService iSharePrefService;


    @BeforeAll
    static void initDataBase(@Autowired DataSource dataSource) throws SQLException {

        Connection con = dataSource.getConnection();
        Statement statement = con.createStatement();

        statement.execute("CREATE TABLE TSHARE(ID_SHARE INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "MESSAGE VARCHAR(16777216)," +
                "SHARE_TYPE VARCHAR(16777216) NOT NULL," +
                "CONTENT_SHARE VARCHAR(16777216) NOT NULL," +
                "USER_SOURCE VARCHAR(50) NOT NULL," +
                "NAME VARCHAR(50) NOT NULL)");

        statement.executeUpdate("INSERT INTO TSHARE VALUES(1,'Mensaje de test','ClaveDelComponente','ContenidoACompartir','demo','Test')");
        statement.executeUpdate("INSERT INTO TSHARE VALUES(2,'Mensaje de test','ClaveDelComponente','ContenidoACompartir','pablo.martinez','Compartido Conmigo')");

        statement.execute("CREATE TABLE TSHARE_TARGET(ID_SHARE_TARGET INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
                "ID_SHARE INTEGER NOT NULL," +
                "USER_TARGET VARCHAR(50) NOT NULL)");

        statement.executeUpdate("INSERT INTO TSHARE_TARGET VALUES(1,1,'pablo.martinez')");
        statement.executeUpdate("INSERT INTO TSHARE_TARGET VALUES(2,1,'noadmin')");
        statement.executeUpdate("INSERT INTO TSHARE_TARGET VALUES(3,2,'democif')");
        statement.executeUpdate("INSERT INTO TSHARE_TARGET VALUES(4,2,'demo')");

    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) throws SQLException {

        Connection con = dataSource.getConnection();
        Statement statement = con.createStatement();

        statement.executeUpdate("DROP TABLE TSHARE");
        statement.executeUpdate("DROP TABLE TSHARE_TARGET");

    }

    @Nested
    class ShareCRUD {

        @Test
        void when_shareQuery_receive_keysValues_and_attributes_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("ID_SHARE", 1);

            List<String> attributes = new ArrayList();
            attributes.add("ID_SHARE");
            attributes.add("MESSAGE");
            attributes.add("SHARE_TYPE");
            attributes.add("CONTENT_SHARE");
            attributes.add("USER_SOURCE");
            attributes.add("NAME");


            EntityResult result = iSharePrefService.shareQuery(keysValues, attributes);

            assertEquals(2, result.calculateRecordNumber());
            Map recordValues = result.getRecordValues(0);

            assertEquals(1, recordValues.get("ID_SHARE"));

            assertEquals("Test", recordValues.get("NAME"));
        }


        @Test
        void when_shareInsert_receive_attributes_expected_EntityResult() {

            Map<String, Object> attributes = new HashMap();
            attributes.put("ID_SHARE", 3);
            attributes.put("MESSAGE", "mensaje");
            attributes.put("SHARE_TYPE", "Clave");
            attributes.put("CONTENT_SHARE", "contenido");
            attributes.put("USER_SOURCE", "lucia");
            attributes.put("NAME", "compartido");

            iSharePrefService.shareInsert(attributes);

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("MESSAGE", "mensaje");

            List<String> attributesList = new ArrayList();
            attributesList.add("ID_SHARE");
            attributesList.add("MESSAGE");
            attributesList.add("SHARE_TYPE");
            attributesList.add("CONTENT_SHARE");
            attributesList.add("USER_SOURCE");
            attributesList.add("NAME");

            EntityResult eResultQuery = iSharePrefService.shareQuery(keysValues, attributesList);
            Map recordValues = eResultQuery.getRecordValues(0);

            assertEquals(3, recordValues.get("ID_SHARE"));
            assertEquals("compartido", recordValues.get("NAME"));

        }

        @Test
        void when_shareUpdate_receive_attributesValues_and_keysValues_expected_EntityResult() {

            Map<String, Object> attributesValues = new HashMap();
            attributesValues.put("NAME", "Update");

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("ID_SHARE", 2);

            iSharePrefService.shareUpdate(attributesValues, keysValues);

            List<String> attributesList = new ArrayList();
            attributesList.add("ID_SHARE");
            attributesList.add("NAME");

            EntityResult eResultQuery = iSharePrefService.shareQuery(keysValues, attributesList);

            Map recordValues = eResultQuery.getRecordValues(0);

            assertEquals(2, recordValues.get("ID_SHARE"));
            assertEquals("Update", recordValues.get("NAME"));
        }

        @Test
        void when_shareDelete_receive_keysValues_and_attributesValues_expected_EntityResult() {

            Map<String, Object> keysValues = new HashMap();
            keysValues.put("ID_SHARE", 3);

            iSharePrefService.shareDelete(keysValues);

            List<String> attributesList = new ArrayList();
            attributesList.add("ID_SHARE");

            EntityResult eResultQuery = iSharePrefService.shareQuery(keysValues, attributesList);

            assertNull(eResultQuery.getRecordValues(0).get("ID_SHARE"));
        }

    }
}