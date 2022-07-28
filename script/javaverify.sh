#!/bin/bash

export CLASSPATH=verification-java.jar:lib/json-20220320.jar:lib/slf4j-api-1.7.12.jar:lib/log4j-1.2.17.jar:lib/slf4j-log4j12-1.7.12.jar

java -classpath $CLASSPATH com.newrelic.labs.java.verify.Main

