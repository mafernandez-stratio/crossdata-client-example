/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.jfairy.Fairy;
import org.jfairy.producer.BaseProducer;
import org.jfairy.producer.person.Person;

import com.stratio.crossdata.common.exceptions.ConnectionException;
import com.stratio.crossdata.common.exceptions.ManifestException;
import com.stratio.crossdata.common.manifest.CrossdataManifest;
import com.stratio.crossdata.common.result.Result;
import com.stratio.crossdata.driver.BasicDriver;
import com.stratio.crossdata.sh.utils.ConsoleUtils;

public class ClientExample {
    public static void main(String[] args) {

        //final String CASSANDRA_DATASTORE_MANIFEST = "/etc/sds/connectors/cassandra/CassandraDataStore.xml";
        final String CASSANDRA_DATASTORE_MANIFEST =
                "/home/mafernandez/workspace/stratio-connector-cassandra/src/main/resources/com/stratio/connector/cassandra/CassandraDataStore.xml";
        //final String CASSANDRA_CONNECTOR_MANIFEST = "/etc/sds/connectors/cassandra/CassandraConnector.xml";
        final String CASSANDRA_CONNECTOR_MANIFEST =
                "/home/mafernandez/workspace/stratio-connector-cassandra/src/main/resources/com/stratio/connector/cassandra/CassandraConnector.xml";

        final String DEEP_CONNECTOR_MANIFEST = "/etc/sds/connectors/deep/DeepConnector.xml";
        final boolean ADD_CASSANDRA_CONNECTOR = true;

        //final boolean ADD_DEEP_CONNECTOR = true;
        final boolean ADD_DEEP_CONNECTOR = false;

        final boolean INSERT_RANDOM_DATA = false;
        final int NUMBER_OF_ROWS = 999;
        final String INSERTS_FILE = "demo.xdql";

        BasicDriver basicDriver = new BasicDriver();
        String username = System.getProperty("user.name");
        basicDriver.setUserName(username);

        Result result = null;
        try {
            result = basicDriver.connect(username);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Connected to Crossdata Server");

        // RESET SERVER DATA
        result = basicDriver.cleanMetadata();
        assert result != null;
        System.out.println("Server data cleaned");

        // ADD CASSANDRA DATASTORE MANIFEST
        CrossdataManifest manifest = null;
        try {
            manifest = ConsoleUtils.parseFromXmlToManifest(
                    CrossdataManifest.TYPE_DATASTORE,
                    CASSANDRA_DATASTORE_MANIFEST);
        } catch (ManifestException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert manifest != null;

        result = null;
        try {
            result = basicDriver.addManifest(manifest);
        } catch (ManifestException e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Datastore manifest added.");

        // ATTACH CLUSTER
        result = null;
        try {
            result = basicDriver.executeQuery("ATTACH CLUSTER cassandra_prod ON DATASTORE Cassandra WITH OPTIONS " +
                    "{'Hosts': '[127.0.0.1]', 'Port': 9042};");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Cluster attached.");

        // ADD STRATIO CASSANDRA CONNECTOR MANIFEST
        if(ADD_CASSANDRA_CONNECTOR){
            manifest = null;
            try {
                manifest = ConsoleUtils.parseFromXmlToManifest(
                        CrossdataManifest.TYPE_CONNECTOR,
                        CASSANDRA_CONNECTOR_MANIFEST);
            } catch (ManifestException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert manifest != null;

            result = null;
            try {
                result = basicDriver.addManifest(manifest);
            } catch (ManifestException e) {
                e.printStackTrace();
            }
            assert result != null;
            System.out.println("Stratio Cassandra Connector manifest added.");
        }

        // ADD STRATIO DEEP CONNECTOR MANIFEST
        if(ADD_DEEP_CONNECTOR){
            manifest = null;
            try {
                manifest = ConsoleUtils.parseFromXmlToManifest(
                        CrossdataManifest.TYPE_CONNECTOR,
                        DEEP_CONNECTOR_MANIFEST);
            } catch (ManifestException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert manifest != null;
            System.out.println("Stratio Deep Connector manifest added.");

            result = null;
            try {
                result = basicDriver.addManifest(manifest);
            } catch (ManifestException e) {
                e.printStackTrace();
            }
            assert result != null;
            System.out.println("Stratio Cassandra Connector manifest added.");
        }

        // ATTACH STRATIO CASSANDRA CONNECTOR
        if(ADD_CASSANDRA_CONNECTOR){
            result = null;
            try {
                result = basicDriver.executeQuery("ATTACH CONNECTOR CassandraConnector TO cassandra_prod WITH OPTIONS {'DefaultLimit': '1000'};");
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert result != null;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Stratio Cassandra connector attached.");
        }

        // ATTACH STRATIO DEEP CONNECTOR
        if(ADD_DEEP_CONNECTOR){
            result = null;
            try {
                result = basicDriver.executeQuery("ATTACH CONNECTOR DeepConnector TO cassandra_prod WITH OPTIONS {};");
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert result != null;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Stratio Deep connector attached.");
        }

        // CREATE CATALOG
        result = null;
        try {
            result = basicDriver.executeQuery("CREATE CATALOG catalogTest;");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Catalog created.");

        // USE
        basicDriver.setCurrentCatalog("catalogTest");

        // CREATE TABLE
        result = null;
        try {
            result = basicDriver.executeQuery("CREATE TABLE tableTest ON CLUSTER cassandra_prod " +
                    "(id int PRIMARY KEY, serial int, name text, rating double, email text);");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Table created.");

        // INSERT RANDOM DATA
        if(INSERT_RANDOM_DATA){
            Fairy fairy = Fairy.create();
            BaseProducer baseProducer = fairy.baseProducer();
            StringBuilder sb;
            for(int i = 1; i<NUMBER_OF_ROWS+1; i++){
                Person person = fairy.person();
                sb = new StringBuilder("INSERT INTO tableTest(id, serial, name, rating, email) VALUES (");
                sb.append(i).append(", ");
                sb.append(generateSerial(baseProducer)).append(", ");
                sb.append(generateName(person)).append(", ");
                sb.append(generateRating(baseProducer)).append(", ");
                sb.append(generateEmail(person));
                sb.append(");");

                result = null;
                try {
                    result = basicDriver.executeQuery(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                assert result != null;
                System.out.println("Row inserted.");
            }
        // INSERT DATA FROM FILE
        } else {
            BufferedReader input = null;
            String query;
            try {
                InputStream scriptFile = ClassLoader.getSystemClassLoader().getResourceAsStream(INSERTS_FILE);
                input = new BufferedReader(new InputStreamReader(scriptFile));
                while ((query = input.readLine()) != null) {
                    query = query.trim();
                    if (query.length() > 0 && !query.startsWith("#")) {
                        basicDriver.executeQuery(query);
                        System.out.println("Row inserted.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // CREATE DEFAULT INDEX
        result = null;
        try {
            result = basicDriver.executeQuery("CREATE INDEX indexTest ON tableTest(name);");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Default index created.");

        // CREATE FULL TEXT INDEX
        result = null;
        try {
            result = basicDriver.executeQuery("CREATE FULL_TEXT INDEX myIndex ON tableTest(email);");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        System.out.println("Full text index created.");

        // CLOSE DRIVER
        try {
            basicDriver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int generateSerial(BaseProducer baseProducer) {
        return baseProducer.randomBetween(1, Integer.MAX_VALUE-1);
    }

    private static String generateName(Person person) {
        return "'" + person.username() + "'";
    }

    private static double generateRating(BaseProducer baseProducer) {
        double rating = baseProducer.randomBetween(0.0, 10.0);
        DecimalFormat f = new DecimalFormat("##.##");
        return Double.parseDouble(f.format(rating));
    }

    private static String generateEmail(Person person) {
        return "'" + person.email() + "'";
    }

}
