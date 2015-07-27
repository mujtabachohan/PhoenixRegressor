# PhoenixRegressor
Phoenix Performance Regression Test Suite - v1.0-SNAPSHOT

## Build
mvn package

## Execute
``java -jar PhoenixRegressor-1.0-SNAPSHOT-jar-with-dependencies.jar``

Setting are in _settings.json_ file:
```
{
"TEST":"test",
"RESULT":"result",
"CONNECTION_STRING":"jdbc:phoenix:<zookeeper>",
"RESULT_JSON_PATH":"result",
"PUBLISH_PATH":"publish",
"ITERATIONS":5,
"LOAD_DATA":"true",
"LABEL":"v4.3"
}
```
Note:
* This uses test JSONs located at [_TEST_](https://github.com/mujtabachohan/PhoenixRegressor/tree/master/src/main/resources/test) directory. These test GSONs include Phoenix DDL, DML and basic rules for data upserts.
* _RESULT_ path stores individual test result GSONs
* __PUBLISH PATH_ path stores HTML results. See _HTML Publishing_ section below
* _ITERATIONS_ number of iterations to run query for and pick the lowest execution time
* _LOAD DATA_ reload synthetic CSV data

If you want to compare say, two different Phoenix versions, change Phoenix version in _pom.xml_ and update label in _settings.json_, compile and execute. Also do update your HBase server with new Phoenix jar, wipe all tables and restart server before you begin your test. Now you would get multiple result files in RESULT directory based on different labels that can be used for comparison.

## HTML Publishing
``java -jar PhoenixRegressor-1.0-SNAPSHOT-jar-with-dependencies.jar publish``

This will produce comparison graphs in HTML located at _PUBLISH_PATH_ based on all the result files available in RESULT directory

