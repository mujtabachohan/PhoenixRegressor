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
* __PUBLISH_ path stores HTML results. See _HTML Publishing_ section below
* If you want to compare say, two different Phoenix versions, change Phoenix version in _pom.xml_ and update label in _settings.json_, compile and execute.
Now you would get multiple result files that can be used for comparison.

## HTML Publishing
``java -jar PhoenixRegressor-1.0-SNAPSHOT-jar-with-dependencies.jar publish``

This will produce comparison graphs in HTML located at _PUBLISH_PATH_

