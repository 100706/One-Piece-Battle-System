#!/usr/bin/env bash
# Deploy One Piece Battle System to Apache Tomcat
set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_DIR="/Users/srijannanduri/apache-tomcat"

echo "========================================================="
echo "   Deploying One Piece Battle System to Apache Tomcat    "
echo "========================================================="

echo "[1/4] Building Maven WAR package..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests

echo "[2/4] Stopping running Tomcat instance (if active)..."
"$TOMCAT_DIR/bin/shutdown.sh" 2>/dev/null || true
sleep 2

echo "[3/4] Deploying WAR to Tomcat webapps..."
rm -rf "$TOMCAT_DIR/webapps/battle-simulation-system" "$TOMCAT_DIR/webapps/battle-simulation-system.war"
cp "$PROJECT_DIR/target/battle-simulation-system.war" "$TOMCAT_DIR/webapps/battle-simulation-system.war"

# Also deploy as root context for convenience
rm -rf "$TOMCAT_DIR/webapps/ROOT" "$TOMCAT_DIR/webapps/ROOT.war"
cp "$PROJECT_DIR/target/battle-simulation-system.war" "$TOMCAT_DIR/webapps/ROOT.war"

echo "[4/4] Starting Apache Tomcat..."
"$TOMCAT_DIR/bin/startup.sh"

echo "========================================================="
echo " Deployment Complete!"
echo " Application URLs:"
echo "   - http://localhost:8080/battle-simulation-system/"
echo "   - http://localhost:8080/"
echo "========================================================="
