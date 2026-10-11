#!/usr/bin/env bash
TOMCAT_DIR="/Users/srijannanduri/apache-tomcat"
echo "Starting Apache Tomcat..."
"$TOMCAT_DIR/bin/startup.sh"
echo "Tomcat running at http://localhost:8080/battle-simulation-system/ and http://localhost:8080/"
