#!/bin/sh

mvn clean package -DskipTests=true

echo rm -rf /usr/local/tomcat/webapps/jpetstore*
rm -rf /usr/local/tomcat/webapps/jpetstore*

echo cp target/jpetstore.war /usr/local/tomcat/webapps/jpetstore.war
cp target/jpetstore.war /usr/local/tomcat/webapps/jpetstore.war