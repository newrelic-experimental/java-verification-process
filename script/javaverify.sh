#!/bin/bash

export CLASSPATH=verification-java.jar:lib/json-20220320.jar:lib/slf4j-api-1.7.12.jar:lib/log4j-1.2.17.jar:lib/slf4j-log4j12-1.7.12.jar:lib/aws-java-sdk-sns-1.11.1000.jar:lib/aws-java-sdk-core-1.11.1000.jar:lib/commons-logging-1.2.jar:lib/aws-java-sqs-1.11.1000.jar:lib/jmespath-java-1.11.1000.jar:lib/jackson-core-2.6.7.jar:lib/jackson-databind-2.6.7.4.jar:lib/jackson-dataformat-cbor-2.6.7.jar:lib/jackson-annotations-2.6.0.jar:lib/httpclient-4.5.13.jar:lib/httpcore-4.4.13.jar:lib/ion-java-1.0.2.jar:lib/joda-time-2.8.1.jar:lib/nanohttpd-2.3.1.jar

java -classpath $CLASSPATH com.newrelic.labs.java.verify.Main

