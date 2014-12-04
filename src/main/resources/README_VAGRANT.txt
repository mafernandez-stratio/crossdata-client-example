README
======================================================================

First steps to initialize services
------------------------------------
0. sudo su -

#Start crossdata
1. cd /etc/init.d
2. service crossdata start

#Start Cassandra
3. service cassandra start

#Start Connectors
4. service connector_cassandra start
5. service connector_deep start

#At this point, we have all that we need so now we start crossdata shell
5. cd /opt/sds/crossdata/bin
6. ./crossdata-sh


Playing with the shell
-----------------------------------
This is an example of crossdata use:

The first stpes are to attach the connectors...

1. ADD DATASTORE /etc/sds/connectors/cassandra/CassandraDataStore.xml;
2. ATTACH CLUSTER cassandra_prod ON DATASTORE Cassandra WITH OPTIONS {'Hosts': '[127.0.0.1]', 'Port': 9042};
3. ADD CONNECTOR /etc/sds/connectors/cassandra/CassandraConnector.xml;
4. ADD CONNECTOR/etc/sds/connectors/deep/DeepConnector.xml;
5. ATTACH CONNECTOR CassandraConnector TO cassandra_prod WITH OPTIONS {'DefaultLimit': '1000'};
6. ATTACH CONNECTOR DeepConnector TO cassandra_prod WITH OPTIONS {};

Now we can operate as usual...

7. CREATE CATALOG catalogTest;
8. USE catalogTest;         
9. CREATE TABLE catalogTest.tableTest ON CLUSTER cassandra_prod (id int PRIMARY KEY, name text);
9. CREATE TABLE catalogTest.tableTest2 ON CLUSTER cassandra_prod (id int PRIMARY KEY, age int);

10. INSERT INTO catalogTest.tableTest(id, name) VALUES (1, 'Peter');
11. INSERT INTO catalogTest.tableTest(id, name) VALUES (2, 'Charles');
12. INSERT INTO catalogTest.tableTest(id, name) VALUES (3, 'Anna');

13. INSERT INTO catalogTest.tableTest2(id, age) VALUES (1, 23);
14. INSERT INTO catalogTest.tableTest2(id, age) VALUES (2, 35);
15. INSERT INTO catalogTest.tableTest2(id, age) VALUES (3, 42);

16. SELECT * FROM catalogTest.tableTest;
17. SELECT id, age FROM catalogTest.tableTest2;

18. SELECT name, age FROM catalogtest.tabletest INNER JOIN catalogtest.tabletest2 ON tabletest.id=tabletest2.id;
